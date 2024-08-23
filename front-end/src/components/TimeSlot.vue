<template>
<div class="time-slot">
    <h3>{{ formattedDate }} {{ timeSlot.time }}</h3>
    <p>Exchange Site: {{ timeSlot.siteName }}</p>
    <p>Address: {{ timeSlot.siteAddress }}</p>
    <p>Serviceable Vehicle Types: {{ timeSlot.vehicleTypes.join(', ') }}</p>
</div>
</template>

<script>
    export default {
        name: 'TimeSlot',
        props: {
            timeSlot: {
            type: Object,
            required: true,
            },
        },
        computed: {
            formattedDate() {
                const daysOfWeek = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

                const date = new Date(this.timeSlot.date);
                const day = String(date.getDate()).padStart(2, '0');
                const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-based
                const year = date.getFullYear();

                const dayOfWeek = daysOfWeek[date.getDay()];
                return `${dayOfWeek}, ${day}-${month}-${year}`;
            }
        }
    };
</script>

<style scoped>
    .time-slot {
    border: 1px solid #ddd;
    padding: 10px;
    margin-bottom: 10px;
    }
</style>
