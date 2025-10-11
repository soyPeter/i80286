You are absolutely correct! My apologies for the oversight. I got carried away with the OpenVPN configuration itself and completely missed the broader context, including the EC2 instance setup, the VPC and networking configurations, permissions changes, and the client setup.

Let's create a more comprehensive, yet concise, technical recap that includes all those aspects.

**Concise Technical Recap: Complete OpenVPN Setup on AWS**

This recap includes all steps, from the AWS infrastructure to the client configuration for OpenVPN.

**I. AWS Infrastructure Setup (Manual)**

### Optional Steps (Unless this is the first thing you do)
1.  **Create a VPC:**
  *  Create a new VPC in the desired AWS region.
  *  Note the VPC CIDR block, as it will be needed for the OpenVPN configuration.
2.  **Create Subnets:**
  *  Create a public subnet in a specific availability zone.
  *  Create private subnets for your private resources, including the database.
  *  Create a db subnet for your database resources.
3. **Create a Internet Gateway:**
  * Create a new Internet Gateway, and attach it to the VPC.
4. **Create default route table:**
* Modify the default route table of your VPC, and add a route to the internet, via the Internet Gateway.
* 
### Mandatory steps
5.  **Create a Security Group (for OpenVPN):**
  *  Create a new security group named `openvpn-sg`
  *   **Inbound Rules:**
    *   `SSH` (TCP, port 22) from your public IP address.
    *   `Custom UDP` (UDP, port 1194) from `0.0.0.0/0`.
  *   **Outbound Rules:** Allow all outbound traffic.

6.  **Launch an EC2 Instance (OpenVPN Server):**
  *   Use a free-tier eligible Ubuntu instance.
  *   Select a Public subnet in your VPC.
  *   Select the previously created `openvpn-sg` security group.
  *   Use an existing or new Key Pair to connect with ssh.

**II. OpenVPN Server Setup (on EC2 Instance)**

1.  **Connect via SSH:**
    ```bash
    ssh -i "your-key-pair.pem" ubuntu@your_public_ip
    ```

2.  **Install OpenVPN and EasyRSA:**
    ```bash
    sudo apt update
    sudo apt install openvpn easy-rsa
    ```

3.  **Prepare EasyRSA:**
    ```bash
     sudo make-cadir /etc/openvpn/easy-rsa
     sudo chmod -R 755 /etc/openvpn/easy-rsa
     sudo cp /usr/share/easy-rsa/vars /etc/openvpn/easy-rsa/
     cd /etc/openvpn/easy-rsa
     sudo ./easyrsa init-pki
    ```

4.  **Generate Diffie-Hellman Parameters:**
    ```bash
    sudo ./easyrsa gen-dh
    ```

5.  **Create Certificate Authority (CA):**
    ```bash
    sudo ./easyrsa build-ca nopass
    ```

6.  **Create Server Certificate and Key:**
    ```bash
    sudo ./easyrsa build-server-full server nopass
    ```

7.  **Create Client Certificate and Key (for your user):**
    ```bash
    sudo ./easyrsa build-client-full devs-profile nopass
    ```

8.  **Copy the required files:**
    ```bash
    sudo cp pki/ca.crt /etc/openvpn/
    sudo cp pki/issued/server.crt /etc/openvpn/
    sudo cp pki/private/server.key /etc/openvpn/
    sudo cp pki/issued/devs-profile.crt /etc/openvpn/
    sudo cp pki/private/devs-profile.key /etc/openvpn/
    sudo cp pki/dh.pem /etc/openvpn/
    ```

9.  **Create OpenVPN user and group:**
      ```bash
      sudo groupadd --system openvpn
      sudo useradd --system -g openvpn --no-create-home openvpn
      ```

10.  **Set permissions for files:**
     ```bash
       sudo chown root:openvpn /etc/openvpn/*
       sudo chmod 640 /etc/openvpn/ca.crt
       sudo chmod 640 /etc/openvpn/server.crt
       sudo chmod 600 /etc/openvpn/server.key
       sudo chmod 640 /etc/openvpn/devs-profile.crt
       sudo chmod 600 /etc/openvpn/devs-profile.key
       sudo chmod 640 /etc/openvpn/dh.pem
     ```
11.  **Create `/etc/openvpn/server.conf` file:**

```
port 1194
proto udp
dev tun
ca /etc/openvpn/ca.crt
cert /etc/openvpn/server.crt
key /etc/openvpn/server.key
dh /etc/openvpn/dh.pem
server 10.8.0.0 255.255.255.0
ifconfig-pool-persist ipp.txt
keepalive 10 120
comp-lzo
user openvpn
group openvpn
persist-key
persist-tun
status openvpn-status.log
verb 3
client-to-client
push "route 172.31.0.0 255.255.0.0" # Your VPC CIDR
```
* Remember to replace the `172.31.0.0 255.255.0.0` with your VPC CIDR.

How to Allow Access to Multiple Databases via VPN

Here are some ways to manage access to multiple databases or resources via OpenVPN, along with the pros and cons of each approach:

Include all Subnets in the Route:

Approach: Modify the push "route" line in your /etc/openvpn/server.conf to include all the subnet ranges where your databases or internal services reside.
```
push "route 172.31.0.0 255.255.0.0"
push "route 10.0.1.0 255.255.255.0"
push "route 10.0.2.0 255.255.255.0"
push "route 10.0.3.0 255.255.255.0"
push "route 10.0.4.0 255.255.255.0"
push "route 10.0.5.0 255.255.255.0"
push "route 10.0.6.0 255.255.255.0"
```
You need to include the CIDR blocks for all your internal networks and subnets.
Pros: Straightforward and easy to implement if you have a predictable set of CIDR blocks to access.
Cons: Can become cumbersome to maintain if you have many internal CIDR blocks, and also when adding or removing new subnets you would need to remember to update the openvpn configuration as well.
If the CIDR blocks do not match, your VPN clients might not have access to the required services.
Push a Larger Supernet:

Another approach could be identifying a supernet (a larger CIDR block) that encompasses all your internal CIDR ranges, and use that in the push "route" line.

12. **Enable IP Forwarding:**
    ```bash
    sudo sysctl -w net.ipv4.ip_forward=1
    sudo sed -i 's/#net.ipv4.ip_forward=1/net.ipv4.ip_forward=1/' /etc/sysctl.conf
    ```

13. **Enable NAT**
    ```bash
    sudo mkdir -p /etc/iptables
    sudo iptables -t nat -A POSTROUTING -s 10.8.0.0/24 -o eth0 -j MASQUERADE
    sudo sh -c "iptables-save > /etc/iptables/rules.v4"
    ```

14. **Enable and start OpenVPN server:**
    ```bash
    sudo systemctl enable openvpn@server
    sudo systemctl start openvpn@server
    ```
15. **Check openvpn status:**
    ```bash
    sudo systemctl status openvpn@server
    sudo journalctl -xeu openvpn@server.service
    ```

**III. OpenVPN Client Configuration**

1.  **Download client files:** Use `scp` to download `ca.crt`, `devs-profile.crt`, and `devs-profile.key` from the EC2 instance.
    ```bash
    scp -i "your-key-pair.pem" ubuntu@your_public_ip:/etc/openvpn/ca.crt .
    scp -i "your-key-pair.pem" ubuntu@your_public_ip:/etc/openvpn/devs-profile.crt .
    scp -i "your-key-pair.pem" ubuntu@your_public_ip:/etc/openvpn/devs-profile.key .
    ```
2.  **Create client configuration:** Create a configuration file named `devs-profile.ovpn` with the content below, and replace `YOUR_CA_CERTIFICATE_CONTENT`, `YOUR_CLIENT_CERTIFICATE_CONTENT` and `YOUR_CLIENT_PRIVATE_KEY_CONTENT` with the content of the files that you have downloaded.
```
client
dev tun
proto udp
remote your_public_ip 1194
resolv-retry infinite
nobind
persist-key
persist-tun
comp-lzo
verb 3
<ca>
-----BEGIN CERTIFICATE-----
YOUR_CA_CERTIFICATE_CONTENT
-----END CERTIFICATE-----
</ca>
<cert>
-----BEGIN CERTIFICATE-----
YOUR_CLIENT_CERTIFICATE_CONTENT
-----END CERTIFICATE-----
</cert>
<key>
-----BEGIN PRIVATE KEY-----
YOUR_CLIENT_PRIVATE_KEY_CONTENT
-----END PRIVATE KEY-----
</key>
redirect-gateway def1
route 172.31.0.0 255.255.0.0
```
* Replace `your_public_ip` with your EC2 instance's public IP.
* Replace `172.31.0.0/16` with your VPC CIDR block.

3. **Import and Test:** Import this configuration to an openvpn compatible client and test the connection and connectivity to your resources in your VPC.
