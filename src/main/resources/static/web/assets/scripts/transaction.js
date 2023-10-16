const { createApp } = Vue;

createApp({
    data() {
        return {
            sourceAccount: [],
            destinationAccount: "",
            originAccount: "",
            description: "",
            amount: 0,
            destination: null,
            loader: true,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("/api/clients/current")
                .then(response => {
                    const accounts = response.data.accounts || [];
                    const activeAccounts = accounts.filter(account => account.isActive);
                    this.sourceAccount = activeAccounts.sort((a, b) => b.balance - a.balance);
                    this.numberAccounts = this.sourceAccount.map(account => account.number);
                    this.loader = false;
                })
                .catch(error => {
                    console.error('Error al obtener las cuentas:', error);
                });
        },        
        addTransaction() {
            Swal.fire({
                title: 'Are you sure you want to proceed with the transaction?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes',
                cancelButtonText: 'No'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post("/api/transactions", `amount=${this.amount}&description=${this.description}&numberOrigin=${this.originAccount}&numberDestiny=${this.destinationAccount}`)
                        .then(response => {
                            Swal.fire({
                                icon: 'success',
                                title: 'Transaction Completed',
                                text: 'The transaction has been completed successfully.',
                            }).then(() => {
                                window.location.href = './accounts.html';
                            });
                        })
                        .catch((error) => {
                            console.error('Error in response:', error.response.data);
                            Swal.fire({
                                icon: 'error',
                                title: 'Transaction Error',
                                text: 'There was an error completing the transaction: ' + error.response.data.message,
                            });
                        });
                } else {
                    Swal.fire({
                        icon: 'info',
                        title: 'Transaction Cancelled',
                        text: 'The transaction has been cancelled.',
                    });
                }
            });
        },        
        logout() {
            Swal.fire({
                title: 'Are you sure you want to log out?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes, log out',
                cancelButtonText: 'No, cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post(`/api/logout`)
                        .then(response => {       
                            Swal.fire({
                                icon: 'success',
                                title: 'Logout Successful',
                                text: 'Your session has been successfully closed.',
                            }).then(() => {
                                window.location.href = '/web/Index.html';
                            });
                        })
                        .catch(error => {
                            console.error('Error logging out', error);
                        });
                }
            });
        },  
    },
}).mount("#app");
