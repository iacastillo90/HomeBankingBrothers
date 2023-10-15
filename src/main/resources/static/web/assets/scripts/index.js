const app = Vue.createApp({
    data() {
      return {
        email:"",
        password:"",
        firstName: "",
        lastName:"",
      };
    },
    methods: {
        login(event) {
            let userEmail = this.email;
            event.preventDefault();
            axios.post('/api/login', `email=${userEmail}&password=${this.password}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
            .then(function (response) {
                console.log(response);
                if (userEmail.includes("@admin.com")) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Login Successful',
                        text: 'Welcome, admin!',
                    }).then(() => {
                        window.location = "/manager.html";
                    });
                } else {
                    Swal.fire({
                        icon: 'success',
                        title: 'Login Successful',
                        text: 'Welcome, user!',
                    }).then(() => {
                        window.location = "./accounts.html";
                    });
                }
            })
            .catch(function (error) {
                console.log(error);
                Swal.fire({
                    icon: 'error',
                    title: 'Login Failed',
                    text: 'Email or username is incorrect. Please re-enter your client details.',
                });
            });
        },
        AddClient() {
            if (this.firstName && this.lastName && this.email.includes("@")) {
                let client = {
                    firstName: this.firstName,
                    lastName: this.lastName,
                    email: this.email,
                    password: this.password,
                };
                axios.post(
                    `http://localhost:8080/api/clients`,
                    `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,
                    { headers: { 'content-type': 'application/x-www-form-urlencoded' } }
                )
                .then(response => {
                    console.log(response);
                    Swal.fire({
                        icon: 'success',
                        title: 'Registration Successful',
                        text: 'Your account has been created successfully.',
                    }).then(() => {
                        window.location = "/web/Index.html";
                    });
                })
                .catch(error => {
                    console.error('Error creating client:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Registration Error',
                        text: 'There was an error creating your account. Please check your input and try again.',
                    });
                });
            }
        },        
        singUp() {
            axios.post(`http://localhost:8080/api/clients`, `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    console.log(response);
                    // Show a success alert using SweetAlert
                    Swal.fire({
                        icon: 'success',
                        title: 'Registration Successful',
                        text: 'Your account has been created successfully.',
                    }).then(() => {
                        window.location = "/web/accounts.html";
                    });
                })
                .catch(error => {
                    console.error('Error creating client:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Registration Error',
                        text: 'There was an error creating your account. Please check your input and try again.',
                    });
                });
        }        
    }
});
  
  app.mount('#app');
  