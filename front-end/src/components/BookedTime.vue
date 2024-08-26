<template>
    <div class="time-slot">
      <div class="static-info">
        <h3>{{ formattedDate }} {{ timeSlot.time }}</h3>
        <p>Exchange Site: {{ timeSlot.siteName }}</p>
        <p>Address: {{ timeSlot.siteAddress }}</p>
        <p>Serviceable Vehicle Types: {{ timeSlot.vehicleTypes.join(', ') }}</p>
        <button @click="eraseSlot" class="erase-btn">Erase</button>
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
        this.$emit('erase', this.timeSlot); // Emit an event to notify parent to remove the slot
      },
    },
  };
  </script>
  
  <style scoped>
  .time-slot {
    margin-bottom: 10px;
  }
  
  .static-info {
    border: 1px solid #ddd;
    padding: 10px;
    background-color: #f9f9f9;
    position: relative; /* Needed for positioning the erase button */
  }
  
  .erase-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    padding: 5px 10px;
    background-color: #dc3545;
    color: white;
    border: none;
    cursor: pointer;
  }
  
  .erase-btn:hover {
    background-color: #c82333;
  }
  </style>
  