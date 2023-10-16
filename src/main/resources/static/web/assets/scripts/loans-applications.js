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
      selectedLoan: null,
      selectedLoanDetails: '',
    };
  },
  created() {
    this.loadData();
    this.loadDataLoans();
  },
  methods: {
    loadData() {
        axios
            .get ("/api/clients/current")
            .then((response) => {
                const accountsData = response.data.accounts;
                this.accounts = accountsData.filter(account => account.isActive);

            })
    
    },
    loadDataLoans() {
      axios
        .get("/api/loans")
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
          Swal.fire({
              icon: 'error',
              title: 'Loan Not Selected',
              text: 'Please select a loan before proceeding.',
          });
          return;
      }
      const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan); 
      if (!selectedLoan) {
          // Manejar el caso en que el préstamo seleccionado no se encuentra.
          Swal.fire({
              icon: 'error',
              title: 'Selected Loan Not Found',
              text: 'The selected loan was not found. Please try again.',
          });
          return;
      }
      this.filterPayments = selectedLoan;
      this.selectedLoanDetails = `Loan Type: ${selectedLoan.name}, Interest Rate: ${selectedLoan.interest}%`;
  },
    sendLoan() {
      if (!this.selectedLoan || !this.numberOrigin || !this.amount || !this.payments) {
          Swal.fire({
              icon: 'error',
              title: 'Incomplete Fields',
              text: 'Please complete all required fields.',
          });
          return;
      }
  
      const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan);
      if (!selectedLoan) {
          Swal.fire({
              icon: 'error',
              title: 'Loan Not Found',
              text: 'The selected loan is not found. Please try again.',
          });
          return;
      }
  
      const loanDetails = {
          typeName: selectedLoan.name, // Use the loan name instead of the ID.
          numberAccountDestination: this.numberOrigin,
          amount: parseFloat(this.amount),
          payments: [this.payments]
      };
  
      Swal.fire({
          title: 'Are you sure you want to add a new loan?',
          icon: 'question',
          showCancelButton: true,
          confirmButtonText: 'Yes, add loan',
          cancelButtonText: 'No, cancel'
      }).then((result) => {
          if (result.isConfirmed) {
              axios
                  .post("/api/loans", loanDetails, {
                      headers: {
                          "Content-Type": "application/json"
                      }
                  })
                  .then((response) => {
                      // Show a success alert using SweetAlert
                      Swal.fire({
                          icon: 'success',
                          title: 'Loan Added Successfully',
                          text: 'The loan has been added successfully.',
                      }).then(() => {
                          location.reload();
                      });
                  })
                  .catch((error) => {
                      console.error('Error adding the loan:', error);
                      if (error.response) {
                          console.error('Server Response:', error.response.data);
                      }
                      // Show an error alert using SweetAlert
                      Swal.fire({
                          icon: 'error',
                          title: 'Error Adding Loan',
                          text: 'There was an error adding the loan. Please try again later.',
                      });
                  });
          }
          // If the user clicks "No" or cancels, no action is taken.
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
                        window.location.href = '/web/index.html';
                    });
                })
                .catch(error => {
                    console.error('Error logging out', error);
                });
        }
    });
    }, 
    calculateInterestAmount() {
        if (!this.selectedLoan || !this.amount) {
            return 0;
        }

        const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan); 
        if (!selectedLoan) {
            return 0;
        }

        const interestRate = selectedLoan.interest;
        const loanAmount = parseFloat(this.amount);

        // Calcular el interés en dinero
        const interestAmount = (interestRate / 100) * loanAmount;

        return interestAmount.toFixed(2); // Formatear el resultado a dos decimales
    }, 
  }
});

app.mount('#app');
