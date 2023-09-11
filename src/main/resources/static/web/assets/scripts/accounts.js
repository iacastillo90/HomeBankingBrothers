const app = Vue.createApp({
  data() {
    return {
      clienteName: '',
      accounts: [],
      clientLoans: '',
      accType: null,
      darkMode: false 
    };
  },
  created() {
      const urlParams = new URLSearchParams(window.location.search);
      var myParam = urlParams.get("id");
      this.loadData()
  },
  methods: {
    loadData() {
      axios.get('http://localhost:8080/api/clients/current')
        .then(response => {
          const clients = response.data;
          console.log(clients)
          this.clienteName = clients.firstName
          this.clientLoans= clients.loans
          this.accounts = response.data.accounts.sort((a, b) => a.id - b.id)
        })
        .catch(error => {
          console.error('Error al obtener los datos de los clientes:', error);
        });
    },
    toggleDarkMode() {
      this.darkMode = !this.darkMode;
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
          window.alert("La cuenta se ha creado con éxito");
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
    }
  }
});

app.mount('#app');
