import matplotlib.pyplot as plt
import pandas as pd
import os

def plot_function_cheb(filename, title):
    data = pd.read_csv(filename)
    plt.figure(figsize=(8, 6))
    plt.plot(data['x'], data['f(x)'], label='f(x)', color='blue')
    plt.plot(data['x'], data['P_n(x)'], label='P_n(x)', color='red', linestyle='dashed')
    plt.xlabel('x')
    plt.ylabel('y')
    plt.title(title)
    plt.legend()
    plt.grid(True)
    plt.show()

# Папка с данными для Чебышевских узлов
data_folder = 'data_cheb'

for file in os.listdir(data_folder):
    if file.endswith('.csv'):
        # Пример имени файла: f1_n2.csv -> func = f1, degree = 2
        func, degree = file.replace('.csv', '').split('_n')
        filepath = os.path.join(data_folder, file)
        title = f"{func} (Чебышев), n = {degree}"
        plot_function_cheb(filepath, title)
