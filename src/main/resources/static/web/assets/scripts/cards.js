const app = Vue.createApp({
    data() {
        return {
            cards: [],
            checkType: null,
            checkColor: null,
            fromDate: "",
            thruDate: "",
            cardsTrue: [],
            card: null,
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
                    title: 'Cards About to Expire',
                    text: 'You have cards that are about to expire in 30 days or less.',
                });
            }
        },
        loadData() {
            axios.get('/api/clients/current')
                .then(response => {
                    const client = response.data;
                    if (client && client.cards) {
                        this.cards = client.cards.map(card => {
                            // Agrega la propiedad cardType según tus criterios
                            card.cardType = this.getCardType(card); // Asume que tienes una función getCardType

                            // Agrega la propiedad cardColor según tus criterios
                            card.cardColor = this.getCardColor(card); // Asume que tienes una función getCardColor

                            return card;
                        });

                        this.fromDate = response.data.cards.map(card => card.fromDate.slice(2, 7).replace(/-/g, '/'))
                        this.thruDate = response.data.cards.map(card => card.thruDate.slice(2, 7).replace(/-/g, '/'))
                        this.cardsTrue = this.cards.filter(card => card.isActive == true);

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
                Swal.fire({
                    icon: 'warning',
                    title: 'Maximum Cards Reached',
                    text: 'You cannot create more than 6 cards.',
                });
                return;
            }
            Swal.fire({
                title: 'Are you sure you want to create a new card?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes, create card',
                cancelButtonText: 'No, cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post("/api/clients/current/cards", null, {
                        params: {
                            cardType: this.checkType,
                            cardColor: this.checkColor
                        }
                    })
                    .then(response => {
                        Swal.fire({
                            icon: 'success',
                            title: 'Card Created',
                            text: 'The card has been created successfully.',
                        }).then(() => {
                            window.location.reload();
                        });
                    })
                    .catch(error => {
                        console.error('Error creating the card:', error);
                        Swal.fire({
                            icon: 'error',
                            title: 'Error Creating Card',
                            text: 'There was an error creating the card.',
                        });
                    });
                }
            });
        },
        deleteCards(id) {
            Swal.fire({
                title: 'Are you sure you want to delete this card?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes, delete card',
                cancelButtonText: 'No, cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.patch(`/api/clients/current/cards/delete/` + id)
                        .then(response => {
                            window.location.reload();
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
        formatDate(date) {
            const options = {  month: 'numeric', year: 'numeric' };
            return new Date(date).toLocaleDateString(undefined, options);
        },
        getCardType(card) {
            if (card.type === 'DEBIT') {
                return 'DEBIT';
            } else if (card.type === 'CREDIT') {
                return 'CREDIT';
            } else {
                return ''; // Valor predeterminado o ningún tipo si no se cumple ninguna condición
            }
        },
        getCardColor(card) {
            if (card.type === 'DEBIT') {
                if (card.color === 'SILVER' || card.color === 'GOLD' || card.color === 'TITANIUM') {
                    return card.color;
                }
            } else if (card.type === 'CREDIT') {
                if (card.color === 'SILVER' || card.color === 'GOLD' || card.color === 'TITANIUM') {
                    return card.color;
                }
            }
            return ''; // Valor predeterminado o ningún color si no se cumple ninguna condición
        },
        getCardClass(cardType) {
            if (cardType === 'DEBIT') {
              return 'debit-card'; // Clase CSS para tarjetas de débito
            } else if (cardType === 'CREDIT') {
              return 'credit-card'; // Clase CSS para tarjetas de crédito
            } else {
              return ''; // Clase CSS predeterminada o ninguna si no se cumple ninguna condición
            }
        },
    },
});

app.mount('#app');
