<template>
    <Transition name = "fade">
    <div :class="{ 'time-slot': true, 'fade-out': fadeOut }">        
        <!-- Static Information Rectangle (clickable) -->
        <div class="static-info" @click="toggleDetails">
            <h3>{{ formattedDate }} {{ timeSlot.time }}</h3>
            <p>Exchange Site: {{ timeSlot.siteName }}</p>
            <p>Address: {{ timeSlot.siteAddress }}</p>
            <p>Serviceable Vehicle Types: {{ timeSlot.vehicleTypes.join(', ') }}</p>
        </div>
    
        <!-- Expandable Booking Rectangle -->
        <div v-if="isExpanded" class="slot-details">
            <input
            type="text"
            v-model="contactInformation"
            placeholder="Enter your contact information"
            class="contact-input"
            :disabled="isBooking"
            />
            <div class="booking-actions">
            <button @click="bookTimeSlot" class="book-btn" :disabled="isBooking">
                Book Time
            </button>
            <span v-if="bookingMessage" class="booking-message">{{ bookingMessage }}</span>
            </div>
        </div>
    </div>
    </Transition>
</template>
  
  <script>
  export default {
    name: "TimeSlot",
    props: {
      timeSlot: {
        type: Object,
        required: true,
      },
      showSlot: {
        type: Boolean,
        required: true,
      }
    },
    data() {
      return {
        isExpanded: false, // Control whether the details section is shown
        contactInformation: "", // Model for the contact information input
        bookingMessage: "", // Message to display after attempting to book
        isBooking: false, // Track if booking is in progress
        fadeOut: false            
      };
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
        const month = String(date.getMonth() + 1).padStart(2, "0"); // Months are zero-based
        const year = date.getFullYear();
  
        const dayOfWeek = daysOfWeek[date.getDay()];
        return `${dayOfWeek}, ${day}-${month}-${year}`;
      },
    },
    methods: {
      toggleDetails() {
        this.isExpanded = !this.isExpanded; // Toggle the details section visibility
      },
      bookTimeSlot() {
        if (!this.contactInformation.trim()) {
          this.bookingMessage = "Contact information is required.";
          return;
        }
  
        this.isBooking = true; // Disable the button and input
        this.bookingMessage = "Waiting for response..."; // Show waiting message
  
        const { timeSlotId, siteId } = this.timeSlot;
  
        fetch(
          `http://localhost:8080/tire-exchange/book-time?siteId=${siteId}&bookId=${timeSlotId}&contactInformation=${this.contactInformation}`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
            },
          }
        )
          .then((response) => response.json())
          .then((data) => {
            this.bookingMessage = data.statusMessage; // Update the booking message based on response
            if (data.statusCode === 200) {  
                
                this.fadeOut = true;

              // Emit the booked slot information to the parent component
              this.$emit('booked', {
                ...this.timeSlot,
                contactInformation: this.contactInformation
              });
            }
          })
          .catch((error) => {
            this.bookingMessage = "An error occurred while booking the time.";
            console.error("Error booking time slot:", error);
            this.isBooking = false; // Re-enable the button and input
          });
      },
    },
  };
  </script>
  
  <style scoped>
  .time-slot {
    border: 1px solid #ddd;
    padding: 10px;
    margin-bottom: 10px;        
    transition: opacity 2s ease;
  }
  
  .static-info {
    cursor: pointer;
  }
  
  .slot-details {
    margin-top: 10px;
  }
  
  .contact-input {
    width: calc(100% - 22px); /* Adjusted width to fit within the container */
    padding: 8px;
    margin-bottom: 10px;
  }
  
  .book-btn {
    padding: 8px 12px;
    background-color: #28a745;
    color: white;
    border: none;
    cursor: pointer;
  }
  
  .book-btn:disabled {
    background-color: #6c757d;
    cursor: not-allowed;
  }
  
  .booking-message {
    color: red;
    margin-top: 10px;
  }

  .contact-input {
  transition: all 0.3s ease-out;
}

.fade-out {
    opacity: 0;
}
  </style>
  