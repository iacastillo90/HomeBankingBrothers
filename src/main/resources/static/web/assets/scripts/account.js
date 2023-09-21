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
        };
    },
    created() {
        this.accountId = new URLSearchParams(window.location.search).get('id');
        this.loadTransactions();
        this.loadAccountNumbers();
    },
    methods: {
        loadTransactions() {
            if (!this.accountId) {
                this.transactions = [];
                return;
            }
            axios.get(`http://localhost:8080/api/accounts/${this.accountId}`)
                .then(response => {
                    this.transactions = response.data.transactions;
                    this.transactions.sort((a, b) => b.id - a.id);
                    
                })
                .catch(error => {
                    console.error('Error al obtener las transacciones:', error);
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
              return ''; // Ningún cambio de estilo para otros tipos
            }
          },
        formatAmount(amount) {
            // Aquí puedes formatear el monto si es necesario.
            return amount.toFixed(2); // Por ejemplo, formateado a dos decimales.
        },
        formatDateTime(dateTime) {
            // Aquí puedes formatear la fecha y hora si es necesario.
            return new Date(dateTime).toLocaleString(); // Utilizando el formato local.
        },
        logout() {
            axios.post(`http://localhost:8080/api/logout`)
                .then(response => {
                    // Redirige al usuario a la página de inicio de sesión
                    window.location.href = '/web/Index.html';
                })
                .catch(error => {
                    console.error('Error al cerrar sesión', error);
                });
        },
        
        generatePDF() {
            const queryParams = new URLSearchParams({
                dateInit: this.dateInit,
                dateEnd: this.dateEnd,
                numberAccount: this.numberAccount,
            });

            const url = `http://localhost:8080/api/transactions/findDate?${queryParams.toString()}`;

            axios.get(url, {
                responseType: "blob"
            })
                .then(response => {
                    const pdfBlob = new Blob([response.data], { type: "application/pdf" });
                    const pdfUrl = URL.createObjectURL(pdfBlob);
                    window.open(pdfUrl);
                })
                .catch(error => {
                    console.error("Error al hacer la solicitud al servlet:", error);
                });
        },
    },
});

app.mount('#app');
