# RuneStone Bank Deployment Guide (Azure VM)

This guide outlines the one-time manual steps required to set up the Azure VM for automated CD via GitHub Actions.

## 1. SSH Deploy Key Setup

To allow GitHub to pull the private repository without using personal credentials, we use a **Deploy Key**.

1.  **SSH into the VM**:
    ```bash
    ssh -i runestone-vm_key debian@<AZURE_VM_IP>
    ```

2.  **Generate a new SSH key pair on the VM**:
    ```bash
    ssh-keygen -t ed25519 -C "github-deploy-key" -f ~/.ssh/id_ed25519_deploy -N ""
    ```

3.  **Add the Public Key to GitHub**:
    - Display the public key: `cat ~/.ssh/id_ed25519_deploy.pub`
    - Copy the output.
    - Go to your GitHub Repository -> **Settings** -> **Deploy keys**.
    - Click **Add deploy key**, give it a title (e.g., `Azure VM Deploy Key`), paste the key, and click **Add key**.

4.  **Add the Private Key to GitHub Secrets**:
    - Display the private key: `cat ~/.ssh/id_ed25519_deploy`
    - Copy the output.
    - Go to GitHub Repository -> **Settings** -> **Secrets and variables** -> **Actions**.
    - Click **New repository secret**.
    - Name: `SSH_PRIVATE_KEY`
    - Value: Paste the private key content.

## 2. GitHub Secrets Inventory

Ensure the following secrets are added to your GitHub Repository (**Settings** -> **Secrets and variables** -> **Actions**):

| Secret Name | Description |
| :--- | :--- |
| `AZURE_VM_HOST` | The Public IP address of your Azure VM |
| `AZURE_VM_USER` | The user name (e.g., `debian`) |
| `SSH_PRIVATE_KEY` | The private key generated in step 1.2 |
| `DOCKERHUB_USERNAME` | Your Docker Hub username |
| `DOCKERHUB_TOKEN` | Your Docker Hub personal access token |
| `APP_PASSWORD` | Mail server application password |
| `JWT_SECRET` | Secret key for JWT generation |
| `DB_PASSWORD` | Password for the PostgreSQL database |

## 3. Initial Repo Clone

Run this once on the VM to clone the repo into the home directory:

```bash
cd ~
git clone git@github.com:<username>/RuneStone-Bank.git
```

*Note: Ensure the local folder name matches what's used in the CI/CD script.*
