import numpy as np

def solve_fd(N=10):
    h = 1 / N
    x_nodes = np.linspace(0, 1, N+1)

    # собираем систему
    a = np.zeros(N+1); b = np.zeros(N+1); c = np.zeros(N+1); F = np.zeros(N+1)
    b[0], c[0], F[0] = -1/h + 1, 1/h, 0
    a[N], b[N], F[N] = -1/h, 1/h - 1, 2
    for i in range(1, N):
        xi = x_nodes[i]
        a[i] = 1/h**2
        b[i] = -2/h**2 - (xi**2 + 1)
        c[i] = 1/h**2
        F[i] = -xi**5 - xi**3 + 6*xi + 2

    U = progonka_algorithm(a, b, c, F)
    return x_nodes, U

def progonka_algorithm(a, b, c, d):
    n = len(d)
    c_ = np.zeros(n)
    d_ = np.zeros(n)

    c_[0] = c[0] / b[0]
    d_[0] = d[0] / b[0]

    for i in range(1, n):
        denom = b[i] - a[i] * c_[i-1]
        c_[i] = c[i] / denom if i != n-1 else 0
        d_[i] = (d[i] - a[i] * d_[i-1]) / denom

    x = np.zeros(n)
    x[-1] = d_[-1]
    for i in range(n - 2, -1, -1):
        x[i] = d_[i] - c_[i] * x[i + 1]

    return x

N = 10000
h = 1 / N
x_nodes = np.linspace(0, 1, N+1)

a = np.zeros(N+1)
b = np.zeros(N+1)
c = np.zeros(N+1)
F = np.zeros(N+1)

# Левая граница: -u'(0) + u(0) = 0 → (-1/h + 1)u0 + (1/h)u1 = 0
b[0] = -1/h + 1
c[0] = 1/h
F[0] = 0

# Правая граница: u'(1) - u(1) = 2 → (-1/h)u_{N-1} + (1/h - 1)u_N = 2
a[N] = -1/h
b[N] = 1/h - 1
F[N] = 2

# Внутренние узлы
for i in range(1, N):
    xi = x_nodes[i]
    a[i] = 1 / h**2
    b[i] = -2 / h**2 - (xi**2 + 1)
    c[i] = 1 / h**2
    F[i] = -xi**5 - xi**3 + 6*xi + 2
    
    
print("СЛАУ")

for i in range(N + 1):
    row = []
    for j in range(N + 1):
        if j == i - 1:
            row.append(f"{a[i]:10.4f}")  # нижняя диагональ
        elif j == i:
            row.append(f"{b[i]:10.4f}")  # главная диагональ
        elif j == i + 1:
            row.append(f"{c[i]:10.4f}")  # верхняя диагональ
        else:
            row.append(f"{0:10.4f}")     # нули вне диагоналей
    row_str = " ".join(row)
    print(f"{row_str} | {F[i]:10.4f}")

    


U = progonka_algorithm(a, b, c, F)

print("\nРезультат решения методом прогонки:")
print("----------------------------------")
print("   x         u(x)")
for xi, ui in zip(x_nodes, U):
    print(f"{xi:.4f} | {ui:.6f}")
