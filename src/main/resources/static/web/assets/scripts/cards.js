const app = Vue.createApp({
    data() {
        return {
            cards: [],
            checkType: null,
            checkColor: null,
            fromDate: "",
            thruDate: "",
            cardsTrue: []
        };
    },
    created() {
        this.loadData();
        this.$nextTick(() => {
            // Llamada a checkExpiryAlert después de cargar SweetAlert2
            this.checkExpiryAlert();
        });
    },
    methods: {
        checkExpiryAlert() {
        const currentDate = new Date(); // Obtén la fecha actual
    
        const expiringSoonCards = this.cardsTrue.filter(card => {
            const thruDate = new Date(card.thruDate); // Convierte la fecha de vencimiento de la tarjeta en un objeto Date
            const daysUntilExpiration = Math.ceil((thruDate - currentDate) / (1000 * 60 * 60 * 24)); // Calcula los días hasta el vencimiento
    
            // Filtra las tarjetas que están próximas a vencer (30 días o menos)
            return daysUntilExpiration <= 30 && daysUntilExpiration >= 0;
        });
    
        if (expiringSoonCards.length > 0) {
            // Mostrar una alerta o cambiar el estilo de las tarjetas expiradas
            // Por ejemplo, puedes mostrar una alerta usando una librería de notificaciones como SweetAlert2
            Swal.fire({
                icon: 'warning',
                title: 'Tarjetas Próximas a Vencer',
                text: 'Tienes tarjetas que están próximas a vencer en 30 días o menos.',
            });
        }
    },
        loadData() {
            axios.get('http://localhost:8080/api/clients/current')
                .then(response => {
                    const client = response.data;
                    if (client && client.cards) {
                        this.cards = client.cards;
                        this.fromDate = response.data.cards.map(card => card.fromDate.slice(2, 7).replace(/-/g, '/'))
                        this.thruDate = response.data.cards.map(card => card.thruDate.slice(2, 7).replace(/-/g, '/'))
                        this.cardsTrue = this.cards.filter(card => card.isActive == true)
                        
                        // Comprueba si alguna tarjeta está próxima a vencer
                        this.checkExpiryAlert();
                    } else {
                        console.error('The response from the request is empty or does not have the "cards" property.');
                    }
                })
                .catch(error => {
                    console.error('Error fetching card data:', error);
                });
        },
        newCard() {
            if (this.cardsTrue.length >= 6) {
                // Alert if the maximum number of cards is reached
                window.alert("You cannot create more than 6 cards.");
                return;
            }

            axios.post("/api/clients/current/cards", null, {
                params: {
                    cardType: this.checkType,
                    cardColor: this.checkColor
                }
            })
            .then(response => {
                window.alert("The card has been created successfully.");
                return window.location.reload();
            })
            .catch(error => {
                console.error('Error creating the card:', error);
            });
        },
        deleteCards(id) {
            axios.patch(`/api/clients/current/cards/delete/` + id)
                .then(response => {
                    return window.location.reload();
                })
        },
        logout() {
            axios.post(`http://localhost:8080/api/logout`)
              .then(response => {
                // Redirect the user to the login page
                window.location.href = '/web/Index.html';
              })
              .catch(error => {
                console.error('Error logging out', error);
              });
        },
        formatDate(date) {
            const options = {  month: 'numeric', year: 'numeric' };
            return new Date(date).toLocaleDateString(undefined, options);
        },
    },
});

app.mount('#app');
