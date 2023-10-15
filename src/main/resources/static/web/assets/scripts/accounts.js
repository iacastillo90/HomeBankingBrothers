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
                interest = loan.amount * 0.25; // 25% de interés para préstamos automotrices
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
      Swal.fire({
          title: 'Are you sure you want to create this account?',
          icon: 'question',
          showCancelButton: true,
          confirmButtonText: 'Yes, create account',
          cancelButtonText: 'No, cancel'
      }).then((result) => {
          if (result.isConfirmed) {
              axios
                  .post(
                      "/api/clients/current/accounts",
                      "typeAccounts=" + this.accType,
                      { headers: { 'content-type': 'application/x-www-form-urlencoded' } }
                  )
                  .then(response => {
                      console.log(response.data);  
                      Swal.fire({
                          icon: 'success',
                          title: 'Account Created',
                          text: 'The account has been created successfully.',
                      }).then(() => {
                          location.reload();
                      });
                  })
                  .catch(error => {
                      console.error(error);  
                      Swal.fire({
                          icon: 'error',
                          title: 'Error Creating Account',
                          text: 'There was an error creating the account.',
                      });
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
              axios.post(`http://localhost:8080/api/logout`)
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
    deleteAccount(id) {
    Swal.fire({
        title: 'Are you sure you want to delete this account?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it',
        cancelButtonText: 'No, cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            axios.patch(`/api/clients/current/accounts/delete/` + id)
                .then(response => {
                    Swal.fire({
                        icon: 'success',
                        title: 'Account Deleted',
                        text: 'The account has been deleted successfully.',
                    }).then(() => {
                        window.location.reload();
                    });
                })
                .catch(error => {
                    console.error('Error deleting account', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error Deleting Account',
                        text: 'There was an error deleting the account.',
                    });
                });
        } else {
            Swal.fire({
                icon: 'info',
                title: 'Deletion Cancelled',
                text: 'The account deletion has been cancelled.',
            });
        }
    });
    },
  }
});

app.mount('#app');
