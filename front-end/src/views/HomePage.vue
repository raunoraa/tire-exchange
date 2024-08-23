<template>
  <div>
    <h2>Available Time Slots</h2>

    <!-- Filters Section -->
    <div class="filters">
      <!-- Filter by Site -->
      <div class="filter-section">
        <h3>Filter by Site</h3>
        <div v-for="site in sites" :key="site.id">
          <input
            type="checkbox"
            :id="'site-' + site.id"
            :value="site.id"
            v-model="selectedSiteIDs"
          />
          <label :for="'site-' + site.id">{{ site.name }}</label>
        </div>
      </div>

      <!-- Filter by Date Range -->
      <div class="filter-section">
        <h3>Filter by Date Range</h3>
        <label for="fromDate">From:</label>
        <input
          type="date"
          id="fromDate"
          v-model="fromDate"
        />
        <label for="toDate">To:</label>
        <input
          type="date"
          id="toDate"
          v-model="toDate"
        />
      </div>

      <!-- Filter by Vehicle Types -->
      <div class="filter-section">
        <h3>Filter by Vehicle Types</h3>
        <div v-for="type in vehicleTypes" :key="type">
          <input
            type="checkbox"
            :id="'type-' + type"
            :value="type"
            v-model="selectedVehicleTypes"
          />
          <label :for="'type-' + type">{{ type }}</label>
        </div>
      </div>

      <!-- Apply Filters Button -->
      <button @click="applyFilters">Apply Filters</button>
    </div>

    <!-- Time Slots Section -->
    <div v-if="timeSlots.length > 0" class="scrollable-container">
      <TimeSlot
        v-for="slot in timeSlots"
        :key="generateKey(slot)"
        :time-slot="slot"
      />
    </div>
    <div v-else>
      <p>No available time slots.</p>
    </div>
  </div>
  </template>
  
<script>
  import TimeSlot from '../components/TimeSlot.vue';
  
  export default {
    name: 'HomePage',
    components: {
      TimeSlot
    },
    data() {
      return {
        timeSlots: [], // This will be populated with data from the backend
        selectedSiteIDs: [], // Array of selected site IDs (strings)
        fromDate: '', // Date string in YYYY-MM-DD format
        toDate: '', // Date string in YYYY-MM-DD format
        selectedVehicleTypes: [], // Array of selected vehicle types
        sites: [], // List of site objects { id: string, name: string }
        vehicleTypes: [] // List of vehicle types
      };
    },
    methods: {
      generateKey(slot) {
        // Generate a unique key by combining timeSlotId and siteId
        return `${slot.id}-${slot.siteId}`;
      },
      fetchTimeSlotsInitial() {        
        
        // Make an API request to fetch available time slots
        fetch('http://localhost:8080/tire-exchange/available-times')
          .then(response => response.json())
          .then(data => {            
            
            // Populate the timeSlots array with the data from the backend
            this.timeSlots = data;            
            
          })
          .catch(error => {
            console.error("Error fetching time slots:", error);
          });
      },
      fetchTimeSlots() {
        // Construct query parameters based on filters
        const queryParams = new URLSearchParams();
        if (this.selectedSiteIDs.length) {
          queryParams.append('siteIDs', this.selectedSiteIDs.join(','));
        }
        if (this.fromDate) {
          queryParams.append('fromDate', this.fromDate);
        }
        if (this.toDate) {
          queryParams.append('toDate', this.toDate);
        }
        if (this.selectedVehicleTypes.length) {
          queryParams.append('vehicleTypes', this.selectedVehicleTypes.join(','));
        }

        // Create the URL with query parameters
        const url = `http://localhost:8080/tire-exchange/available-times${queryParams.toString() ? '?' + queryParams.toString() : ''}`;

        // Make an API request to fetch available time slots with filters
        fetch(url)
          .then(response => response.json())
          .then(data => {
            this.timeSlots = data;
          })
          .catch(error => {
            console.error("Error fetching time slots:", error);
          });
      },
      applyFilters() {
        this.fetchTimeSlots();
      },
      populateFilterOptions(data) {
        // Extract unique sites and vehicle types from the data
        this.sites = [...new Set(data.map(slot => ({
          id: slot.siteId,
          name: slot.siteName
        })))];
        this.vehicleTypes = [...new Set(data.flatMap(slot => slot.vehicleTypes))];
      }
    },
    mounted() {
      // Fetch the time slots when the component is mounted
      this.fetchTimeSlotsInitial();
    }
  };
</script>
  
<style scoped>
  .scrollable-container {
    width: 80vh;
    height: 50vh; /* Adjust the height as needed */
    overflow-y: auto; /* Enables vertical scrolling */
    border: 1px solid #ddd; /* Optional: adds a border for visual separation */
    padding: 10px; /* Optional: adds padding inside the container */
  }
</style>
  