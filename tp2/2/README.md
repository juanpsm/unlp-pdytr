# 2)

Levantar máquinas desde la [raiz](../) de este repositorio:

```shell
host$ vagrant up
```

Se crean las maquinas `vm1` y `vm2` en una red privada usando las ips
relacionadas al default interface, `192.168.*.101` y `192.168.*.102` respectivamente.

## a)

Se ingresa por ssh a las vm:

```shell
host$ vagrant ssh vm1
vagrant@vm1 cd /vagrant/2
vagrant@vm1:/vagrant/2$ ./server-run.sh
```

```shell
host$ vagrant ssh vm2
vagrant@vm2 cd /vagrant/2
vagrant@vm2:/vagrant/2$ ./client-sun.sh > tiempos-vm-vm.txt
```

Se pueden ver los resultados en [dicho archivo](./tiempos-vm-vm.txt)

## b)

Lo realizamos dos veces, primero con el host como cliente.

```shell
host$ vagrant ssh vm1
vagrant@vm1 cd /vagrant/2
vagrant@vm1:/vagrant/2$ ./server-run.sh
```

```shell
host$ cd 2
host$ ./client-run.sh > tiempos-vm-host.txt
```

Se pueden ver los resultados en [dicho archivo](./tiempos-vm-host.txt)

Luego el host cumplió el rol de server

```shell
host$ cd 2
host$ ./server-run.sh
```

```shell
vagrant ssh vm2
vagrant@vm2$ cd /vagrant/2
vagrant@vm2:/vagrant/2$ ./client-run-host-ip.sh > tiempos-host-vm.txt
```

Se pueden ver los resultados en [dicho archivo](./tiempos-host-vm.txt)
