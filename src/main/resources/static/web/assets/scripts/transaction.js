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
                    this.sourceAccount = response.data.accounts.sort((a, b) => b.balance - a.balance)
                    this.numberAccounts = this.sourceAccount.map(account => account.number)
                    this.loader = false
                })
        },
        addTransaction() {
            if (confirm('¿Estás seguro de que deseas realizar la transacción?')) {
                axios.post("/api/transactions", `amount=${this.amount}&description=${this.description}&numberDestiny=${this.originAccount}&numberOrigin=${this.destinationAccount}`)
                    .then(response => {
                        alert('¡Transacción realizada!');
                        location.href = './accounts.html';
                    })
                    .catch((error) => {
                        console.error('Error en la respuesta:', error.response.data);
                        alert('Error al realizar la transacción: ' + error.response.data.message); 
                    });
            } else {
                alert('La transacción no se realizó.');
            }
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
}).mount("#app");
