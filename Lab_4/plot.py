import matplotlib.pyplot as plt
import pandas as pd
import os

def plot_function(filename, title):
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

# Папка с данными
data_folder = 'data'
for file in os.listdir(data_folder):
    if file.endswith('.csv'):
        func, degree = file.replace('.csv', '').split('_n')
        plot_function(os.path.join(data_folder, file), f"{func}, n = {degree}")
