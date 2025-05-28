import numpy as np
import matplotlib.pyplot as plt

from Lab_7.fd.lab4_1 import solve_fd
from Lab_7.shoot.lab4_2 import solve_shoot

# Параметр сетки
N = 10000

# Получаем решения
x_fd, U_fd     = solve_fd(N)
x_shoot, U_shoot = solve_shoot(N)

# Если узлы не совпадают, делаем интерполяцию:
if not np.allclose(x_fd, x_shoot):
    U_shoot_on_fd = np.interp(x_fd, x_shoot, U_shoot)
else:
    U_shoot_on_fd = U_shoot

# Разность (абсолютная или с учётом знака)
difference = U_shoot_on_fd - U_fd

# Рисуем
plt.figure(figsize=(8,5))
plt.plot(x_fd, difference, marker='o', label='u_shoot − u_fd')
plt.title('Разность решений: стрельба – прогонка')
plt.xlabel('x')
plt.ylabel('u_shoot(x) - u_fd(x)')
plt.grid(True)
plt.legend()
plt.tight_layout()
plt.show()
