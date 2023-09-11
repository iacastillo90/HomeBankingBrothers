const app = Vue.createApp({
    data() {
        return {
            transactions: [],
            accountId: null,
        };
    },
    watch: {
        accountId: 'loadTransactions',
    },
    created() {
        this.accountId = new URLSearchParams(window.location.search).get('id');
        this.loadTransactions ();
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
        getClass(type) {
            return {
                'table-success': type === 'CREDIT',
                'table-danger': type === 'DEBIT',
            };
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
          }
    },
});

app.mount('#app');

