import psutil
print(psutil.virtual_memory()[1] / 1024 / 1024 / 1024)
