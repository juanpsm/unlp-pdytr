# Install local gRCP for c++

## Setup

Choose a directory to hold locally installed packages. This page assumes that the environment variable MY_INSTALL_DIR holds this directory path. For example:
```
$ export MY_INSTALL_DIR=$PWD/.local
$ mkdir -p $MY_INSTALL_DIR
$ export PATH="$MY_INSTALL_DIR/bin:$PATH"
```
## Install cmake & other tools

You need version 3.13 or later of cmake. Install it by following these instructions:

### Linux
``` 
$ sudo apt install -y cmake build-essential autoconf libtool pkg-config
$ sudo pacman -S cmake build-essential autoconf libtool pkg-config
$ cmake --version
cmake version 3.19.6
```
## Clone gRCP repo
```
$ git clone --recurse-submodules -b v1.58.0 --depth 1 --shallow-submodules https://github.com/grpc/grpc
```
## Build gRPC & tooling 

```
$ cd grpc
$ mkdir -p cmake/build
$ pushd cmake/build
$ cmake -DgRPC_INSTALL=ON \
      -DgRPC_BUILD_TESTS=OFF \
      -DCMAKE_INSTALL_PREFIX=$MY_INSTALL_DIR \
      ../..
$ make -j 4
$ make install
$ popd
```
## Build example 

```
$ cd examples/cpp/helloworld
$ mkdir -p cmake/build
$ pushd cmake/build
$ cmake -DCMAKE_PREFIX_PATH=$MY_INSTALL_DIR ../..
$ make -j 4
```
## Run example

Terminal 1:
```
./greeter_server
```
Terminal 2:
```
$ ./greeter_client
Greeter received: Hello world
```
