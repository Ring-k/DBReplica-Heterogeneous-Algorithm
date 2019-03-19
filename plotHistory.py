import numpy as np
from matplotlib import pyplot as plt

def main():
    file = open('history', 'r')
    line = file.read()
    data = line.strip().split('\n')
    floatdata = []
    for i in data:
        floatdata.append(float(i))
    file.close()
    floatdata = np.array(floatdata)
    print(floatdata)
    plt.plot(floatdata, linewidth=0.5)
    plt.savefig("plot.png")
    plt.show()


if __name__ == '__main__':
    main()