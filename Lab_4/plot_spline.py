import matplotlib.pyplot as plt
import pandas as pd
import os

def plot_spline(filename, title):
    data = pd.read_csv(filename)
    plt.figure(figsize=(8, 6))
    plt.plot(data['x'], data['f(x)'], label='f(x)', color='blue')
    plt.plot(data['x'], data['Spline(x)'], label='Spline(x)', color='red', linestyle='dashed')
    plt.xlabel('x')
    plt.ylabel('y')
    plt.title(title)
    plt.legend()
    plt.grid(True)
    plt.show()

# Папка, где сохранены CSV-файлы с данными сплайнов
data_folder = 'data_spline'

for file in os.listdir(data_folder):
    if file.endswith('.csv'):
        # Имена файлов вида: f1_n2.csv, f1_n4.csv, и т.д.
        func, deg = file.replace('.csv', '').split('_n')
        title = f"{func} - Кубический сплайн, n = {deg}"
        filepath = os.path.join(data_folder, file)
        plot_spline(filepath, title)
