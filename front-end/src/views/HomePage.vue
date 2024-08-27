<template>
  <div class="homepage">
    <h2>Time Slots</h2>

    <!-- Toggle Switch for View Selection -->
    <div class="view-toggle">
      <button @click="viewMode = 'available'" :class="{ active: viewMode === 'available' }">Available Time Slots</button>
      <button @click="viewMode = 'booked'" :class="{ active: viewMode === 'booked' }">Booked Time Slots</button>
    </div>

    <!-- Content Container -->
    <div class="content-container">
      <!-- Filters Section (Visible only in available mode) -->
      <div v-if="viewMode === 'available'" class="filters-container">
        <!-- Filter by Site -->
        <div class="filter-results-section">
          <div class="filters-heading" @click="toggleFilters">
            Filter Results
            <span class="toggle-icon">{{ filtersVisible ? 'v' : '>' }}</span>
          </div>
          <div v-if="filtersVisible" class="filters">
            <!-- Filter by Site -->
            <div class="filter-section">
              <h4>Include Sites</h4>
              <div class="filter-group">
                <div v-for="site in sites" :key="site.siteId" class="filter-item">
                  <input
                    type="checkbox"
                    :id="'site-' + site.siteId"
                    :value="site.siteId"
                    v-model="selectedSiteIDs"
                  />
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
                  <VueDatePicker
                    v-model="fromDate"
                    :min-date="minDate"
                    format="dd/MM/yyyy"
                    placeholder="Select a date"
                    @input="handleFromDateChange"
                    :enable-time-picker="false"
                    model-type="yyyy-MM-dd"
                  />
                  <p v-if="fromDateError" class="error-message">{{ fromDateError }}</p>
                </div>
                <div class="date-picker-group">
                  <label for="toDate">To:</label>
                  <VueDatePicker
                    v-model="toDate"
                    :min-date="fromDate"
                    format="dd/MM/yyyy"
                    placeholder="Select a date"
                    @input="handleToDateChange"
                    :enable-time-picker="false"
                    model-type="yyyy-MM-dd"
                  />
                  <p v-if="toDateError" class="error-message">{{ toDateError }}</p>
                </div>
              </div>
            </div>

            <!-- Filter by Vehicle Types -->
            <div class="filter-section">
              <h4>Include Vehicle Types</h4>
              <div class="filter-group">
                <div v-for="type in vehicleTypes" :key="type" class="filter-item">
                  <input
                    type="checkbox"
                    :id="'type-' + type"
                    :value="type"
                    v-model="selectedVehicleTypes"
                  />
                  <label :for="'type-' + type">{{ type }}</label>
                </div>
              </div>
              <div class="match-mode">
                <label>Match:</label>
                <select v-model="vehicleTypeMatchMode">
                  <option value="any">Any</option>
                  <option value="all">All</option>
                </select>
              </div>
            </div>

            <!-- Apply Filters Button -->
            <div class="apply-filters">
              <button @click="applyFilters" class="apply-filters-button">Apply Filters</button>
            </div>
          </div>
        </div>

        <!-- Results Heading -->
        <div class="results-section">
          <h3>Results</h3>
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
        </div>
      </div>

      <!-- Booked Time Slots Section (Visible only in booked mode) -->
      <div v-if="viewMode === 'booked'" class="scrollable-container booked-time-slots">
        <h2>Booked Time Slots</h2>
        <BookedTime
          v-for="slot in sortedBookedSlots"
          :key="generateKey(slot)"
          :time-slot="slot"
          @erase="removeBookedSlot"
        />
        <div v-if="!bookedSlots.length">
          <p>No booked time slots.</p>
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
      filtersVisible: false // New property to handle filter section visibility
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
        })
        .catch(error => {
          console.error("Error fetching time slots:", error);
        });
    },
    applyFilters() {
      if (!this.fromDate || !this.toDate) {
        this.initializeDates();
      }

      if (this.validateDates()) {
        this.fetchTimeSlots();
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
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.view-toggle {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.view-toggle button {
  padding: 10px 15px;
  margin: 0 5px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.view-toggle .active {
  background-color: #007bff;
  color: white;
}

/* Container for filters and time slots */
.content-container {
  display: flex;  
  gap: 30px;
}

/* Filters Section */
.filters-container {
  display: flex;
  flex-direction: column;
  width: 300px;
}

.filter-results-section {
  border: 2px solid #007bff;
  border-radius: 8px;
  padding: 10px;
  transition: max-height 0.3s ease;
}

.filters-heading {
  font-size: 1.5em; /* Same font size as Results heading */
  font-weight: bold;
  color: black; /* Set text color to black */
  text-align: center;
  cursor: pointer;
}

.filters-heading:hover {
  color: #0056b3;
}

.filters-heading:active {
  color: #003d7a;
}

.filters {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.filters-container .filters {
  max-height: 500px; /* Adjust height based on the content */
}

.results-section {
  flex: 1;  
  /* You can probably modify the width with media queries properly. Use vw and vh for adjusting it. */
}

.scrollable-container {
  max-height: 55vh;
  overflow-y: auto;
}

.booked-time-slots {
  margin-top: 20px;
}

/* Media Queries */
@media (min-width: 768px) {
  .content-container {
    flex-direction: row;
  }

  .filters-container {
    margin-right: 40px;
  }

  .results-section {
    flex: 3;
  }
}

@media (max-width: 767px) {
  .content-container {
    flex-direction: column;
  }

  .filters-container {
    width: 100%;
    margin-right: 0;
  }

  .results-section {
    flex: 1;
  }
}
</style>
