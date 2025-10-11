
### [K8s](https://es.wikipedia.org/wiki/Kubernetes) for orchestration

Kubernetes tries to automate deployment, scalability and management over server clusters of containerized applications,
this is done via rest api applying resources for apps life cycle.
The application life cycle is specified using declarative specifications (a body payload) and async request via scheduler.

The body payload will contain at least this:
- Kind: object type (ClusterIp | NodePort | LoadBalancer | ExternalName)
- apiVersion: Object structure version
- metadata: Identification data for the object
- spec: Desired config
- status: Object current status

All this is done by a reconciliation loop that drives actual cluster state toward the desired cluster state, communicating with the API
server to create, update, and delete the resources it manages (pods, service endpoints, etc.)

#### Concepts
##### Pod
Basic unit for K8s, can contain one or more containers

##### Service
Pods registry with an external Ip
###### Types
- ClusterIp, default type, obtains a stable IP only accessible from cluster pods
- NodePort, exposes the service with a random port in every node, same port in every node
- LoadBalancer, gets a load balancer from cloud provider and routes traffic to NodePorts

### [Minikube](https://minikube.sigs.k8s.io/docs/start/) for local development

As you can read in the start page of minikube

> Minikube is local Kubernetes, focusing on making it easy to learn and develop for Kubernetes.
> All you need is Docker (or similarly compatible) container or a Virtual Machine environment,
> and Kubernetes is a single command away: minikube start

##### Installation

First we need to install [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-macos/#install-with-homebrew-on-macos). Kubectl is a
CLI for K8s management

In MacOS with brew
```bash
$ brew install minikube
```
How to start a cluster, mandatory docker up and running
```bash
$ minikube start --cpus=4 --memory=2G --driver=docker
```
Other ways to start a cluster:

- Create json config file in user home minkube folder:

```bash
$ cd  ~/.minikube/config && nano config.json
```
Paste this and ctrl + x to save and exit nano
```json
{
  "cpus": 4,
  "disk-size": 20000,
  "driver": "docker",
  "memory": 2000
}
```

```bash
$ start minikube
```

To check if everything is working fine
```bash

$ kubectl get nodes

NAME       STATUS   ROLES                  AGE   VERSION
minikube   Ready    control-plane,master   8h    v1.20.2
```

```bash

$ minikube status

minikube
type: Control Plane
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured
```
##### Useful commands
```bash
$ eval $(minikube docker-env -u)
$ minikube update-check
$ minikube pause
$ minikube unpause
$ minikube stop
$ minikube start
$ minikube delete
$ minikube config
$ minikube config set cpus 4
$ minikube config unset cpus
$ minikube config view
$ minikube config get cpus | memory
$ minikube ip
$ minikube ssh
$ minikube logs -f --problems=true
```

#### [Kubectl (KubeControl)](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)

```bash
$ kubectl + command + resource           + name      + arguments
$ kubectl   create                                   -f definition-file.yml
$ kubectl   get                                      --help
$ kubectl   get       node               minikube    -o wide
$ kubectl   run                          nginx       --image=nginx:latest --port 80 --replicas=1 --restart=Never --dry-run=client -o yaml > pod-nginx.yaml
$ kubectl   describe  pod                nginx
$ kubectl   delete    deployment/pod     nginx
$ kubectl   get       pod                            --show-labels -l project
$ kubectl   exec                                     -it pod-name bash
$ kubectl   cluster-infoh
```
