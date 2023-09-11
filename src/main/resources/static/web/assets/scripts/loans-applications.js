const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      loans: [],
      numberOrigin: null,
      amount: null,
      payments: null,
      accounts: [],
      filterPayments: [],
      selectedLoan: null
    };
  },
  created() {
    this.loadData();
    this.loadDataLoans();
  },
  methods: {
    loadData() {
      axios
        .get("http://localhost:8080/api/clients/current/accounts")
        .then((response) => {
          this.accounts = response.data;
          console.log("Cuentas recuperadas con éxito:", this.accounts);
        })
        .catch((error) => {
          console.error("Error al recuperar cuentas:", error);
        });
    },
    loadDataLoans() {
      axios
        .get("http://localhost:8080/api/loans")
        .then((response) => {
          this.loans = response.data;
          console.log("Préstamos recuperados con éxito:", this.loans);
        })
        .catch((error) => {
          console.error("Error al recuperar préstamos:", error);
        });
    },
    filterLoansPayments() {
        if (!this.selectedLoan) {
            // Manejar el caso en que no se ha seleccionado ningún préstamo.
            window.alert('Please select a loan before proceeding.');
            return;
        }
    
        const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan);
    
        if (!selectedLoan) {
            // Manejar el caso en que el préstamo seleccionado no se encuentra.
            window.alert('Selected loan not found. Please try again.');
            return;
        }
    
        // Si llegamos aquí, el préstamo seleccionado es válido.
        this.filterPayments = selectedLoan;
    },
    sendLoan() {
        if (!this.selectedLoan || !this.numberOrigin || !this.amount || !this.payments) {
          window.alert('Por favor, complete todos los campos obligatorios.');
          return;
        }
      
        // Asegúrate de que selectedLoan sea un objeto que contiene el nombre correcto.
        const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan);
      
        if (!selectedLoan) {
          window.alert('El préstamo seleccionado no se encuentra. Por favor, intente nuevamente.');
          return;
        }
      
        const loanDetails = {
          typeName: selectedLoan.name, // Usar el nombre del préstamo en lugar del ID.
          numberAccountDestination: this.numberOrigin,
          amount: parseFloat(this.amount),
          payments: [this.payments]
        };
      
        const confirmation = window.confirm('¿Desea agregar un nuevo préstamo?');
      
        if (confirmation) {
          axios
            .post("http://localhost:8080/api/loans", loanDetails, {
              headers: {
                "Content-Type": "application/json"
              }
            })
            .then((response) => {
              console.log("Préstamo agregado con éxito:", response.data);
              location.reload();
            })
            .catch((error) => {
              console.error('Error al agregar el préstamo:', error);
              if (error.response) {
                console.error('Respuesta del servidor:', error.response.data);
              }
            });
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
  }
});

app.mount('#app');
