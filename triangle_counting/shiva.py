# Dung Nguyen
# Implement color sampling algorithm
import networkx as nx
import gurobipy as grb
import numpy as np
import math
import random
import shiva
import csv
import sys
from concurrent.futures import ProcessPoolExecutor
import scipy.optimize as opt
# import basic_edge
# import color
import common
import timeit
import time


def linear_program_solve(net, D, p=1, c=0, method="gurobi"):

    if method == "gurobi":
        return linear_program_solve_gurobi(net, D, p, c)

    return linear_program_solve_scipy(net, D, p, c)


def linear_program_solve_scipy(net, D, p=1, c=0):

    triangles = common.list_triangles(net)

    num_triangles = len(triangles)

    number_of_nodes = net.number_of_nodes()

    c = np.ones(num_triangles) * -1

    P = np.zeros((number_of_nodes, num_triangles))

    p = np.ones(number_of_nodes) * (D * (D-1) / 2)

    for i in range(number_of_nodes):
        for j in range(num_triangles):
            if i in triangles[j]:
                P[i][j] = 1

    start = timeit.default_timer()

    lp = opt.linprog(c, A_ub=P, b_ub=p, bounds=(0, 1), method="highs-ds")

    end = timeit.default_timer()
    duration = end - start

    # print("LP Solver time: ", duration)

    return -lp.fun


def linear_program_solve_gurobi(net, D, p=1, c=0):
    num_triangles, triangles, nodes = common.triangle_count(net)

    # Linear Programming Model
    lpm = grb.Model()
    lpm.Params.LogFile = "gurobi.log"
    lpm.Params.LogToConsole = 0
    lpm.Params.Threads = 32

    x = lpm.addVars(num_triangles, name="x_C")

    lpm.addConstrs(x[i] >= 0 for i in range(num_triangles))
    lpm.addConstrs(x[i] <= 1 for i in range(num_triangles))

    # # TODO
    # # May rewrite this part for faster execution
    # for node in net.nodes():
    #     lpm.addConstr(grb.quicksum(x[i]
    #                                for i in range(num_triangles)
    #                                if node in triangles[i]) <= p * D * (D - 1) / 2) # A node in a D-bounded graph can involve in at most 1/2D(D-1) triangles

    for node in nodes.keys():
        lpm.addConstr(grb.quicksum(x[i]
                                   for i in nodes[node]) <= D * (D - 1) / 2 ) #  + c * math.log(net.number_of_nodes())) # A node in a D-bounded graph can involve in at most 1/2D(D-1) triangles

    lpm.setObjective(grb.quicksum(x[i] for i in range(num_triangles)),
                     grb.GRB.MAXIMIZE)

    lpm.optimize()

    return lpm.ObjVal


def shiva_differentially_private_triange_count(net, D, epsilon, method="gurobi", reps=20):
    real_triangle_count = total_triangles(net)

    number_of_nodes = net.number_of_nodes()
    # print("Nodes: ", number_of_nodes)

    threshold = number_of_nodes ** 2 * math.log(number_of_nodes) / epsilon
    # print("Threshold: ", threshold)

    f1_hat = real_triangle_count + np.random.laplace(0, number_of_nodes ** 2 / epsilon, reps)

    # if f1_hat > 7 * threshold:
    #     return f1_hat

    lpm = linear_program_solve(net, D, method=method)
    # print("LP Count: ", lpm)
    rng = np.random.default_rng()
    noise = rng.laplace(0, D ** 2 / epsilon, reps)
    f2_hat = lpm + noise

    # print("F1_hat:", f1_hat)
    # print("Noise: ", noise)

    # print("raw:", f1_hat <= 2 * threshold)

    f_hat = f1_hat * (f1_hat >= 2 * threshold) + (f1_hat < 2 * threshold) * f2_hat

    return f_hat


def total_triangles(G):
    return sum(list(nx.triangles(G).values())) // 3


def average_degree(G):
    return sum(val for (node, val) in G.degree()) / G.number_of_nodes()


def max_degree(G):
    return max(val for (node, val) in G.degree())

network_path = "../data_graphs/"

# network_name = ["ca-GrQc", #5000
#                 "ca-HepTh", #10000
#                 "ca-HepPh", #12000
#                 "ca-AstroPh", #19000
#                 "ca-CondMat", #23133
#                 "email-Enron", #36000
#                 "loc-gowalla_edges", #200000
#                ]

log_file = "shiva.csv"


def main():
    csvfile = open(log_file, 'a')
    result_writer = csv.writer(csvfile, delimiter=',',
                       quotechar='|', quoting=csv.QUOTE_MINIMAL)
    net_name = sys.argv[1]
    epsilon = float(sys.argv[2])
    delta = float(sys.argv[3])
    reps = int(sys.argv[4])
    params = sys.argv[5]
    solver = sys.argv[6]

    D = int(params[1:-1].split(',')[0])
    print("Net:", net_name)
    print("Epsilon:", epsilon)
    print("Delta:", delta)
    print("Repeat:", reps)
    print("D:", D)
    print("Solver:", solver)

    net = nx.read_edgelist(network_path + net_name + ".txt",
                   create_using=nx.Graph(),
                   nodetype=int)

    true_triangle_count = common.triangle_count(net)[0]

    print("True count:", true_triangle_count)

    shiva_outputs = shiva_differentially_private_triange_count(
            net, 
            D, 
            epsilon, 
            method=solver, 
            reps=reps)

    print("DP counts:", shiva_outputs)

    for sample in np.nditer(shiva_outputs):
        result_writer.writerow([time.time(),
                                "node_privacy",
                                "shiva_alg",
                                net_name,
                                true_triangle_count,
                                epsilon,
                                delta,
                                params,
                                sample,
                                ])
 

if __name__ == "__main__":
    main()

