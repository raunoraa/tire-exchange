<template>
    <div class="time-slot">
        <div class="static-info">
            <h3 class="date-info">{{ formattedDate }}</h3>
            <h3 class="date-info">{{ timeSlot.time }}</h3>
            <p class="slot-info">Exchange Site: {{ timeSlot.siteName }}</p>
            <p class="slot-info">Address: {{ timeSlot.siteAddress }}</p>
            <p class="slot-info">Serviceable Vehicle Types: {{ timeSlot.vehicleTypes.join(', ') }}</p>
            <br>
            <h4 class="contact-heading">Provided Contact Information</h4>
            <p class="slot-info">Full Name: {{ timeSlot.contactInformation.fullName }}</p>
            <p class="slot-info">Phone Number: {{ timeSlot.contactInformation.phoneNumber }}</p>
            <p class="slot-info">Email: {{ timeSlot.contactInformation.email }}</p>

            <button @click="eraseSlot" class="erase-btn">Cancel</button>
        </div>
    </div>
</template>


<script>
export default {
    name: "BookedTime",
    props: {
        timeSlot: {
            type: Object,
            required: true,
        },
    },
    computed: {
        formattedDate() {
            const daysOfWeek = [
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
            ];

            const date = new Date(this.timeSlot.date);
            const day = String(date.getDate()).padStart(2, "0");
            const month = String(date.getMonth() + 1).padStart(2, "0");
            const year = date.getFullYear();

            const dayOfWeek = daysOfWeek[date.getDay()];
            return `${dayOfWeek}, ${day}-${month}-${year}`;
        },
    },
    methods: {
        eraseSlot() {
            this.$emit('erase', this.timeSlot);
        },
    },
};
</script>


<style scoped>
.time-slot {
    margin-bottom: 10px;
}

.static-info {
    border: 1px solid #a09f9f;
    padding: 10px;
    background-color: #f9f9f9;
    position: relative;
    border-radius: 4px;
}

.erase-btn {
    position: absolute;
    top: 20px;
    right: 20px;
    background-color: #dc3545;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 4px;
    font-weight: bold;
}

.contact-heading {
    padding-top: 0;
    margin-top: 0;
    margin-bottom: 0;
}

.erase-btn:hover {
    background-color: #c82333;
}

.erase-btn:active {
    background-color: #861823;
}

@media (min-width: 1100px) {

    .date-info {
        font-size: 1.1rem;
    }

    .contact-heading {
        font-size: 1.05rem;
    }

    .slot-info {
        font-size: 1rem;
    }

    .erase-btn {
        padding: 10px 20px;
    }
}

@media (max-width: 1099px) {

    .date-info {
        font-size: 1rem;
    }

    .contact-heading {
        font-size: 0.95rem;
    }

    .slot-info {
        font-size: 0.9rem;
    }

    .erase-btn {
        padding: 7px 10px;
    }
}
</style>