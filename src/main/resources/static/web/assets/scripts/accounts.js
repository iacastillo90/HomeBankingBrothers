const app = Vue.createApp({
  data() {
    return {
      clienteName: '',
      accounts: [],
      clientLoans: '',
      accType: null,
      accountsTrue: [],
    };
  },
  created() {
    const urlParams = new URLSearchParams(window.location.search);
    var myParam = urlParams.get("id");
    this.loadData()
  },
  methods: {
    // Agrega el método formatAmount aquí
    formatAmount(amount) {
      // Aquí puedes formatear el monto si es necesario.
      return amount.toFixed(2); // Por ejemplo, formateado a dos decimales.
    },
    loadData() {
      axios.get('http://localhost:8080/api/clients/current')
        .then(response => {
          const clients = response.data;
          console.log(clients);
          this.clienteName = clients.firstName;
          this.clientLoans = clients.loans.map(loan => {
            // Calcula el interés según el tipo de préstamo
            let interest;
            switch (loan.name) {
              case 'MORTGAGE':
                interest = loan.amount * 0.20; // 20% de interés para hipotecas
                break;
              case 'PERSONAL':
                interest = loan.amount * 0.15; // 15% de interés para préstamos personales
                break;
              case 'AUTOMOTIVE':
                interest = loan.amount * 0.20; // 25% de interés para préstamos automotrices
                break;
              default:
                interest = 0; // Valor predeterminado si el tipo de préstamo no se reconoce
                break;
            }

            return {
              ...loan,
              interest: interest,
            };
          });
          this.accounts = response.data.accounts.sort((a, b) => a.id - b.id);
          this.accountsTrue = this.accounts.filter(accounts => accounts.isActive == true);
          console.log(this.accountsTrue);
        })
        .catch(error => {
          console.error('Error al obtener los datos de los clientes:', error);
        });
    },
    createAccount() {
      axios
        .post(
          "/api/clients/current/accounts",
          "typeAccounts=" + this.accType,
          { headers: { 'content-type': 'application/x-www-form-urlencoded' } }
        )
        .then(response => {
          console.log(response.data);  // Log the response for debugging
          window.alert("The account has been created successfully");
          location.reload();
        })
        .catch(error => {
          console.error(error);  // Log the error for debugging
          window.alert("Error creating account");
        });
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
    deleteAccount(id) {
      axios.patch(`/api/clients/current/accounts/delete/` + id)
          .then(response => {
              return window.location.reload()
          })
    },
  }
});

app.mount('#app');
