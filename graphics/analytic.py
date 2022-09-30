import numpy as np

A = 1.0
M = 70.0
K = 10000
GAMMA = 100.0

def calculateR(t):
    return A * (np.exp(-(GAMMA/(2*M)) * t)) * (np.cos(np.power((K/M) - (GAMMA*GAMMA/(4*(M*M))), 0.5) * t))
