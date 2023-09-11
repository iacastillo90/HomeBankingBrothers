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
                if (userEmail == "admin@mindhub.com") {
                    window.location = "/manager.html";
                } else {
                    window.location = "./accounts.html";
                }
            })
            .catch(function (error) {
                console.log(error);
                alert("Email or username incorrect, please re-enter your client details");
            }); 
        },
        logout() {
            axios.post(`http://localhost:8080/api/logout`)
            .then(response =>  {
                location.href = '/web/Index.html'
            })
            .catch(error => {
                console.error('Error', error);
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
                    alert("Register Ok")
                    this.logout();
                })
                .catch(error => {
                    console.error('Error creating client:', error);
                    alert("Error creating client. Please check your input and try again.");
                });
            }
        },        
        singUp (){
            axios.post(`http://localhost:8080/api/clients`, `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`, {headers:{'content-type':'application/x-www-form-urlencoded'}} )
            .then(response => {
                console.log(response);
                
                 window.location = "/web/accounts.html"
                 return window.location.href = "/web/accounts.html"
            })
            
        }
    }
});
  
  app.mount('#app');
  