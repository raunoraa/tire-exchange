<template>
  <div class="homepage">    

    <!-- Toggle Switch for View Selection -->
    <div class="view-toggle">
      <button @click="viewMode = 'available'" :class="{ active: viewMode === 'available' }">Available Time
        Slots</button>
      <button @click="viewMode = 'booked'" :class="{ active: viewMode === 'booked' }">Booked Time Slots</button>
    </div>

    <!-- Content Container -->
    <div class="content-container">
      <!-- Filters Section (Visible only in available mode) -->
      <div v-if="viewMode === 'available'" class="filters-container">
        <div class="filter-results-section">
          <div class="filters-heading" @click="toggleFilters">
            Filter Results
            <span class="toggle-icon">{{ filtersVisible ? '⇓' : '⇒' }}</span>
          </div>
          <div v-if="filtersVisible" class="filters">
            <!-- Filter by Site -->
            <div class="filter-section">
              <h4>Include Sites</h4>
              <div class="filter-group">
                <div v-for="site in sites" :key="site.siteId" class="filter-item">
                  <input type="checkbox" :id="'site-' + site.siteId" :value="site.siteId" v-model="selectedSiteIDs" />
                  <label :for="'site-' + site.siteId">{{ site.name }}</label>
                </div>
              </div>
            </div>

            <!-- Filter by Date Range -->
            <div class="filter-section">
              <h4>Date Range</h4>
              <div class="filter-group">
                <div class="date-picker-group">
                  <label for="fromDate">From:</label>
                  <VueDatePicker v-model="fromDate" :min-date="minDate" :teleport="true" format="dd/MM/yyyy" placeholder="Select a date"
                    @input="handleFromDateChange" :enable-time-picker="false" model-type="yyyy-MM-dd" />
                  <p v-if="fromDateError" class="error-message">{{ fromDateError }}</p>
                </div>
                <div class="date-picker-group">
                  <label for="toDate">To:</label>
                  <VueDatePicker v-model="toDate" :min-date="fromDate" :teleport="true" format="dd/MM/yyyy" placeholder="Select a date"
                    @input="handleToDateChange" :enable-time-picker="false" model-type="yyyy-MM-dd" />
                  <p v-if="toDateError" class="error-message">{{ toDateError }}</p>
                </div>
              </div>
            </div>

            <!-- Filter by Vehicle Types -->
            <div class="filter-section">
              <h4>Include Vehicle Types</h4>
              <div class="match-mode">
                <label>Match: </label>
                <select v-model="vehicleTypeMatchMode">
                  <option value="any">Any</option>
                  <option value="all">All</option>
                </select>
              </div>
              <div class="filter-group">
                <div v-for="type in vehicleTypes" :key="type" class="filter-item">
                  <input type="checkbox" :id="'type-' + type" :value="type" v-model="selectedVehicleTypes" />
                  <label :for="'type-' + type">{{ type }}</label>
                </div>
              </div>              
            </div>

            <!-- Apply Filters Button -->
            <div class="apply-filters">
              <button @click="applyFilters" class="apply-filters-button" ref="applyButton">Apply Filters</button>
              <span v-if="filtersApplied" class="filters-applied-message" ref="filtersAppliedMessage">Filters Applied!</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Results Section -->
      <div v-if="viewMode === 'available'" class="results-section">
        <h3 class="results-heading">Results</h3>
        <div v-if="isLoading" class="slots-info-container">
          <p class="slots-info-message">Fetching the data...</p>
        </div>
        <div v-else-if="timeSlots.length > 0" class="scrollable-container">
          <TimeSlot v-for="slot in timeSlots" :key="generateKey(slot)" :time-slot="slot" :show-slot="!slot.fadeOut"
            @booked="handleBookedSlot" />
        </div>
        <div v-else class="slots-info-container">
          <p class="slots-info-message">No available time slots found.</p>
        </div>
      </div>


      <!-- Booked Time Slots Section (Visible only in booked mode) -->
      <div v-if="viewMode === 'booked'" class="booked-time-slots">
        <h2 class="booked-times-heading">My Booked Time Slots</h2>
        <div class="scrollable-container">
        <BookedTime v-for="slot in sortedBookedSlots" :key="generateKey(slot)" :time-slot="slot"
          @erase="removeBookedSlot" />
        <div v-if="!bookedSlots.length">
          <p class="booked-slots-info">No booked time slots.</p>
        </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TimeSlot from '../components/TimeSlot.vue';
import BookedTime from '../components/BookedTime.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

export default {
  name: 'HomePage',
  components: {
    TimeSlot,
    BookedTime,
    VueDatePicker
  },
  data() {
    return {
      timeSlots: [],
      bookedSlots: [],
      selectedSiteIDs: [],
      fromDate: '',
      toDate: '',
      minDate: '',
      selectedVehicleTypes: [],
      vehicleTypeMatchMode: 'any',
      sites: [],
      vehicleTypes: [],
      fromDateError: '',
      toDateError: '',
      viewMode: 'available',
      filtersVisible: false,
      isLoading: false,
      filtersApplied: false,
    };
  },
  computed: {
    sortedBookedSlots() {
      return this.bookedSlots.slice().sort((a, b) => {
        const dateComparison = new Date(a.date) - new Date(b.date);
        if (dateComparison !== 0) return dateComparison;
        return this.compareTimes(a.time, b.time);
      });
    }
  },
  methods: {
    generateKey(slot) {
      return `${slot.timeSlotId}-${slot.date}-${slot.time}-${slot.siteId}`;
    },
    fetchTimeSlots() {

      this.isLoading = true;

      const queryParams = new URLSearchParams();

      if (this.fromDate) queryParams.append('from', this.fromDate);
      if (this.toDate) queryParams.append('to', this.toDate);
      if (this.selectedSiteIDs.length) queryParams.append('sites', this.selectedSiteIDs.join(','));
      if (this.selectedVehicleTypes.length) {
        queryParams.append('vehicleTypes', this.selectedVehicleTypes.join(','));
        queryParams.append('vehicleTypeMatchMode', this.vehicleTypeMatchMode);
      }

      const url = `http://localhost:8080/tire-exchange/available-times${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
      console.log('Fetching available time slots from URL:', url);

      fetch(url)
        .then(response => response.json())
        .then(data => {
          this.timeSlots = data;
          this.isLoading = false;
        })
        .catch(error => {
          console.error("Error fetching time slots:", error);
          this.Loading = false;
        });
    },
    applyFilters() {
      if (!this.fromDate || !this.toDate) {
        this.initializeDates();
      }

      if (this.validateDates()) {
        this.fetchTimeSlots();
        this.filtersApplied = true;
        // Set up an event listener to hide the message when clicking outside
        document.addEventListener('click', this.hideFiltersAppliedMessage);
      } else {
        this.filtersApplied = false;
      }
    },
    hideFiltersAppliedMessage(event){
      // Ensure the click is outside the button and the message span
      const applyButton = this.$refs.applyButton;
      const messageSpan = this.$refs.filtersAppliedMessage;

      if (
        applyButton && !applyButton.contains(event.target) &&
        messageSpan && !messageSpan.contains(event.target)
      ) {
        this.filtersApplied = false;
        // Remove the event listener
        document.removeEventListener('click', this.hideFiltersAppliedMessage);
      }
    },
    fetchConfig() {
      fetch('http://localhost:8080/tire-exchange/get-config')
        .then(response => response.json())
        .then(data => {
          this.sites = data.map(site => ({
            siteId: site.siteId,
            name: site.name,
            address: site.address,
            vehicleTypes: site.vehicleTypes
          }));
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
      const today = new Date();
      const futureDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000);

      this.fromDate = this.formatDateToAPI(today);
      this.toDate = this.formatDateToAPI(futureDate);

      this.minDate = today.toISOString().split('T')[0];
    },
    validateDates() {
      this.fromDateError = '';
      this.toDateError = '';

      if (new Date(this.fromDate) > new Date(this.toDate)) {
        this.fromDateError = "The 'From' date cannot be later than the 'To' date.";
        this.fromDate = this.toDate;
      }

      if (new Date(this.toDate) < new Date(this.fromDate)) {
        this.toDateError = "The 'To' date cannot be earlier than the 'From' date.";
        this.toDate = this.fromDate;
      }

      return !this.fromDateError && !this.toDateError;
    },
    handleFromDateChange() {
      if (this.fromDate && new Date(this.fromDate) > new Date(this.toDate)) {
        this.fromDateError = "The 'From' date cannot be later than the 'To' date.";
        this.fromDate = this.toDate;
      } else {
        this.fromDateError = '';
      }
    },
    handleToDateChange() {
      if (this.toDate && new Date(this.toDate) < new Date(this.fromDate)) {
        this.toDateError = "The 'To' date cannot be earlier than the 'From' date.";
        this.toDate = this.fromDate;
      } else {
        this.toDateError = '';
      }
    },
    formatDateToAPI(date) {
      const day = date.getDate().toString().padStart(2, '0');
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const year = date.getFullYear();
      return `${year}-${month}-${day}`;
    },
    handleBookedSlot(slot) {
      this.bookedSlots.push(slot);
      setTimeout(() => {
        this.timeSlots = this.timeSlots.filter(s => s.timeSlotId !== slot.timeSlotId);
      }, 2000);
    },
    removeBookedSlot(slot) {
      this.bookedSlots = this.bookedSlots.filter(s => s !== slot);
    },
    compareTimes(timeA, timeB) {
      const [hourA, minuteA] = timeA.split(':').map(Number);
      const [hourB, minuteB] = timeB.split(':').map(Number);

      if (hourA !== hourB) return hourA - hourB;
      return minuteA - minuteB;
    },
    toggleFilters() {
      this.filtersVisible = !this.filtersVisible;
    }
  },
  created() {
    this.fetchConfig();
    this.initializeDates();
    this.fetchTimeSlots();
  }
};
</script>

<style scoped>
.homepage {
  padding: 10px;
  padding-top: 0;
  width: 100vw;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.view-toggle {
  display: flex;
  justify-content: center;
  margin: 20px;
  margin-bottom: 40px;
}

.view-toggle button {
  padding: 16px 26px;
  margin: 0 5px;
  border: none;
  border-radius: 4px;
  cursor: pointer;  
  font-weight: bold;
  font-size: 1rem;
}

.view-toggle .active {
  background-color: #007bff;
  color: white;
}

.slots-info-container {
  justify-content: center;
}

.slots-info-message {
  color: black;
  text-align: center;
  font-size: 1.25rem;
  margin-top: 10px;
}

/* Container for filters and time slots */
.content-container {
  justify-content: center;
  display: flex;  
  gap: 30px;
  align-items: flex-start;
}

/* Filters Section */
.filters-container {
  display: flex;
  flex-direction: column;  
}

.filter-results-section {
  border: 2px solid #007bff;
  border-radius: 8px;
  padding: 10px;
  transition: max-height 0.3s ease;

  background-color: rgb(208, 252, 197);
}

.filters-heading {
  font-weight: bold;
  color: black;
  text-align: center;
  cursor: pointer;

  padding: 10px;  
  background-color: rgb(255, 218, 218);
  border-radius: 4px;
}

.filters-heading:hover {
  color: #c2d4e7;
  background-color: rgb(172, 131, 131);
}

.filters-heading:active {
  color: #ecf0f5;
  background-color: rgb(99, 75, 75);
}

.filters {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.filters-applied-message{
  margin-left: 10px;
  color: green;
}

.filters-container .filters {
  max-height: 700px;
  margin:5px;
  margin-top: 0;
}

.filter-item{
  margin-bottom:10px;
}

.date-picker-group{
  margin-right: 10px;  
  margin-bottom: 10px;  
}

.filter-section{
  margin-top: 35px;
  margin-bottom: 30px;  
}

.match-mode{  
  margin-bottom: 10px;
}

.match-mode select{
  font-size: 0.9rem;
}

.apply-filters{
  margin: 10px;
}

.apply-filters button{
  padding: 10px 15px;  
  border: 1px solid black;
  border-radius: 4px;
  cursor: pointer;  
  font-weight: bold;
}


.results-section {
  width: 40vw;
  border: 2px solid #007bff;
  border-radius: 8px;
  padding: 10px;
}

.results-heading {
  font-size: 1.5rem;
  text-align: center;
  margin: 10px;
  margin-bottom: 15px;
}

.scrollable-container {
  max-height: 60vh;
  overflow-y: auto;
}

.booked-time-slots {
  margin-top: 20px;
  border: 2px solid blue;
  padding: 20px;
  border-radius: 8px;
  min-width: 400px;
}

.results-section {
  min-width: 400px;
}

.error-message{
  color: red;
}

.booked-times-heading{
  margin-top:0;
  padding-top:0;
}

.booked-slots-info{
  text-align: center;
}

/* Media Queries */
@media (min-width: 1100px) {
  .content-container {
    flex-direction: row;
  }

  .filters-container {
    margin-right: 40px;
    max-width: 450px;
    min-width: 300px;
    width: 25vw;
  }

  .results-section {
    min-width: 500px;
    width: 40vw;
  }

  .results-heading {
    font-size: 1.5rem;
  }

  .filters-heading {
    font-size: 1.25rem;
  }

  .booked-time-slots{
    width: 25vw;
    min-width: 600px;
  }
}

@media (max-width: 1099px) {

  .results-heading {
    font-size: 1.25rem;
  }

  .filters-heading {
    font-size: 1.1rem;
  }

  .content-container {
    flex-direction: column;
    align-items: center;
  }

  .filters-container {
    margin-right: 0;
    width: 100vw;
    max-width: 350px;
    min-width: 250px;
  }

  .results-section {
    width: 70vw;
    min-width: 325px;
  }

  .booked-time-slots{
    width: 70vw;
    min-width: 325px;
  }
}
</style>
