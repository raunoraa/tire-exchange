<template>
  <div>
    <h2>Available Time Slots</h2>

    <!-- Filters Section -->
    <div class="filters">
      <!-- Filter by Site -->
      <div class="filter-section">
        <h3>Filter by Site</h3>
        <div v-for="site in sites" :key="site.siteId">
          <input
            type="checkbox"
            :id="'site-' + site.siteId"
            :value="site.siteId"
            v-model="selectedSiteIDs"
          />
          <label :for="'site-' + site.siteId">{{ site.name }}</label>
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
          :min="minDate"
          :max="maxDate"
          @change="validateFromDate"
        />
        <p v-if="fromDateError" class="error-message">{{ fromDateError }}</p>

        <label for="toDate">To:</label>
        <input
          type="date"
          id="toDate"
          v-model="toDate"
          :min="fromDate"
          :max="maxDate"
          @change="validateToDate"
        />
        <p v-if="toDateError" class="error-message">{{ toDateError }}</p>
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
        <div>
          <label>Match:</label>
          <select v-model="vehicleTypeMatchMode">
            <option value="any">Any</option>
            <option value="all">All</option>
          </select>
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
        :show-slot="!slot.fadeOut"
        @booked="handleBookedSlot"        
      />
    </div>
    <div v-else>
      <p>No available time slots.</p>
    </div>

    <!-- Booked Time Slots Section -->
    <div class="scrollable-container">
      <h2>Booked Time Slots</h2>
      <BookedTime
        v-for="slot in bookedSlots"
        :key="generateKey(slot)"
        :time-slot="slot"        
        @erase="removeBookedSlot"
      />
      <div v-if="!bookedSlots.length">
        <p>No booked time slots.</p>
      </div>
    </div>
  </div>
</template>

<script>
import TimeSlot from '../components/TimeSlot.vue';
import BookedTime from '../components/BookedTime.vue';

export default {
  name: 'HomePage',
  components: {
    TimeSlot,
    BookedTime
  },
  data() {
    return {
      timeSlots: [], // This will be populated with data from the backend
      bookedSlots: [], // This will be populated with booked slots
      selectedSiteIDs: [], // Array of selected site IDs (strings)
      fromDate: '', // Date string in YYYY-MM-DD format
      toDate: '', // Date string in YYYY-MM-DD format
      selectedVehicleTypes: [], // Array of selected vehicle types
      vehicleTypeMatchMode: 'any', // Default to "any"
      sites: [], // List of site objects { siteId: string, name: string, address: string, vehicleTypes: array }
      vehicleTypes: [], // List of unique vehicle types
      minDate: '', // Minimum date for date pickers
      maxDate: '', // Maximum date for date pickers
      fromDateError: '', // Error message for invalid "From" date
      toDateError: '' // Error message for invalid "To" date
    };
  },
  methods: {
    generateKey(slot) {
      return `${slot.timeSlotId}-${slot.date}-${slot.time}-${slot.siteId}`;
    },
    fetchTimeSlots() {
      const queryParams = new URLSearchParams();

      if (this.fromDate) {
        queryParams.append('from', this.fromDate);
      }
      if (this.toDate) {
        queryParams.append('to', this.toDate);
      }
      if (this.selectedSiteIDs.length) {
        queryParams.append('sites', this.selectedSiteIDs.join(','));
      }
      if (this.selectedVehicleTypes.length) {
        queryParams.append('vehicleTypes', this.selectedVehicleTypes.join(','));
        queryParams.append('vehicleTypeMatchMode', this.vehicleTypeMatchMode); // Add match mode
      }

      // Create the URL with query parameters
      const url = `http://localhost:8080/tire-exchange/available-times${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
      console.log('Fetching available time slots from URL:', url);

      // Make an API request to fetch available time slots with filters
      fetch(url)
        .then(response => response.json())
        .then(data => {
          //console.log('Fetched time slots:', data);
          this.timeSlots = data;
        })
        .catch(error => {
          console.error("Error fetching time slots:", error);
        });
    },
    applyFilters() {
      console.log("Filters applied!");
      if (this.validateDates()) {
        this.fetchTimeSlots();
      }
    },
    fetchConfig() {
      // Fetch the exchange sites and vehicle types from the backend
      fetch('http://localhost:8080/tire-exchange/get-config')
        .then(response => response.json())
        .then(data => {
          this.sites = data.map(site => ({
            siteId: site.siteId,
            name: site.name,
            address: site.address,
            vehicleTypes: site.vehicleTypes
          }));
          // Extract unique vehicle types across all exchange sites
          const vehicleTypesSet = new Set();
          data.forEach(site => {
            site.vehicleTypes.forEach(type => vehicleTypesSet.add(type));
          });
          this.vehicleTypes = Array.from(vehicleTypesSet);
        })
        .catch(error => {
          console.error("Error fetching config:", error);
        });
    },
    initializeDates() {
      const today = new Date().toISOString().split('T')[0];
      const futureDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
      this.fromDate = today;
      this.toDate = futureDate;
      this.minDate = today; // Set minimum date to today
      this.maxDate = futureDate; // Set maximum date to 30 days from today
    },
    validateDates() {
      this.fromDateError = '';
      this.toDateError = '';
      if (this.fromDate && new Date(this.fromDate) > new Date(this.toDate)) {
        this.fromDateError = "The 'From' date cannot be later than the 'To' date.";
        this.fromDate = this.toDate; // Adjust the fromDate
      }
      if (this.toDate && new Date(this.toDate) < new Date(this.fromDate)) {
        this.toDateError = "The 'To' date cannot be earlier than the 'From' date.";
        this.toDate = this.fromDate; // Adjust the toDate
      }
      return !this.fromDateError && !this.toDateError;
    },
    handleBookedSlot(slot) {
      // Add the booked slot to the bookedSlots array
      this.bookedSlots = [...this.bookedSlots, slot];
      // Remove the slot from the available slots
      //this.timeSlots = this.timeSlots.filter(s => s.timeSlotId !== slot.timeSlotId);                  

      // Remove the booked slot from available slots after 2 seconds
      setTimeout(() => {              
          this.timeSlots = this.timeSlots.filter(s => s.timeSlotId !== slot.timeSlotId);
              }, 2000);
      // Remove the booked slot from available slots after 2 seconds
      //this.timeSlots = this.timeSlots.filter(s => s.timeSlotId !== slot.timeSlotId);
    },
    removeBookedSlot(slot) {
      // Remove the booked slot from the bookedSlots array
      this.bookedSlots = this.bookedSlots.filter(s => s.timeSlotId !== slot.timeSlotId);
    }
  },
  mounted() {
    // Fetch the configuration data when the component is mounted
    this.fetchConfig();

    // Set initial date range and fetch time slots
    this.initializeDates();
    this.fetchTimeSlots();
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

.filters {
  margin-bottom: 20px; /* Add some space below the filters */
}

.filter-section {
  margin-bottom: 10px;
}

.error-message {
  color: red;
  font-size: 0.9em;
  margin-top: 5px;
}
</style>
