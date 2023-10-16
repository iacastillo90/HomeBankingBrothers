const app = Vue.createApp({
    data() {
        return {
            transactions: [],
            accountId: null,
            dateInit: "",
            dateEnd: "",
            numberAccount: "",
            accountNumbers: [], 
            sourceAccount: [],
            transactionsAndLoans: [],
        };
    },
    created() {
        this.accountId = new URLSearchParams(window.location.search).get('id');
        this.loadTransactionsAndLoans();
        this.loadAccountNumbers();
        
    },
    methods: {
        loadTransactionsAndLoans() {
            axios.get(`/api/accounts/${this.accountId}`)
                .then(response => {
                    // Obtén datos de transacciones y préstamos desde la respuesta
                    const transactions = response.data.transactions || [];
                    const loans = response.data.loans || [];
        
                    // Combina las transacciones y los préstamos en una sola lista
                    this.transactionsAndLoans = [...transactions, ...loans];
                    this.transactionsAndLoans.sort((a, b) => b.id - a.id);
                    console.log(this.transactionsAndLoans);
                })
                .catch(error => {
                    console.error('Error al obtener las transacciones y préstamos:', error);
                });
        },
        loadAccountNumbers() {
            axios.get("/api/clients/current")
                .then(response => {
                    // Obtén los números de cuenta disponibles
                    this.sourceAccount = response.data.accounts.sort((a, b) => b.balance - a.balance)
                    this.numberAccounts = this.sourceAccount.map(account => account.number)
                })
                .catch(error => {
                    console.error('Error al obtener los números de cuenta:', error);
                });
        },
        getTransactionClass(type) {
            if (type === 'DEBIT') {
              return 'text-red';
            } else if (type === 'CREDIT') {
              return 'text-green';
            } else {
              return ''; 
            }
        },          
        formatAmount(amount) {
            return amount.toFixed(2); 
        },
        formatDateTime(dateTime) {
            return new Date(dateTime).toLocaleString(); 
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
        generatePDF() {
            const queryParams = new URLSearchParams({
                dateInit: this.dateInit,
                dateEnd: this.dateEnd,
                numberAccount: this.numberAccount,
            });
            const url = `/api/transactions/findDate?${queryParams.toString()}`;
            axios.get(url, {
                responseType: "blob"
            })
                .then(response => {
                    const pdfBlob = new Blob([response.data], { type: "application/pdf" });
                    const pdfUrl = URL.createObjectURL(pdfBlob);
                    window.open(pdfUrl);
                    Swal.fire({
                        icon: 'success',
                        title: 'Download Successful',
                        text: 'The PDF has been downloaded successfully.',
                    });
                })
                .catch(error => {
                    console.error("Error making request to the servlet:", error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'There was an error generating the PDF.',
                    });
                });
        },        
    },
});

app.mount('#app');
