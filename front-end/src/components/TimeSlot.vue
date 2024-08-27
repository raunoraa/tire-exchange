<template>
    <Transition name="fade">
      <div :class="{ 'time-slot': true, 'fade-out': fadeOut }">
        <!-- Static Information Rectangle (clickable) -->
        <div
          class="static-info"
          @click="toggleDetails"
          @mousedown="handleMouseDown"
          @mouseup="handleMouseUp"
        >
          <h3>{{ formattedDate }} {{ timeSlot.time }}</h3>
          <p>Exchange Site: {{ timeSlot.siteName }}</p>
          <p>Address: {{ timeSlot.siteAddress }}</p>
          <p>Serviceable Vehicle Types: {{ timeSlot.vehicleTypes.join(', ') }}</p>
        </div>
  
        <!-- Expandable Booking Rectangle -->
        <div v-if="isExpanded" class="slot-details">
          <h4>Contact Information</h4>
          
          <div class="input-container">
            <input
              type="text"
              v-model="fullName"
              placeholder="Full Name"
              class="contact-input"
              :disabled="isBooking"
              maxlength="100"
            />
            <span class="char-counter">{{ fullName.length }}/100</span>
          </div>
  
          <div class="input-container">
            <input
              type="text"
              v-model="phoneNumber"
              @input="validatePhoneNumber"
              placeholder="Phone Number"
              class="contact-input"
              :disabled="isBooking"
              maxlength="20"
            />
            <span class="char-counter">{{ phoneNumber.length }}/20</span>
          </div>
  
          <div class="input-container">
            <input
              type="email"
              v-model="email"
              placeholder="Email"
              class="contact-input"
              :disabled="isBooking"
              maxlength="100"
            />
            <span class="char-counter">{{ email.length }}/100</span>
          </div>
  
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
        isExpanded: false,
        fullName: "",
        phoneNumber: "",
        email: "",
        bookingMessage: "",
        isBooking: false,
        fadeOut: false,
        isClicked: false
      };
    },
    computed: {
      formattedDate() {
        const daysOfWeek = [
          "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
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
      toggleDetails() {
        this.isExpanded = !this.isExpanded;
      },
      validatePhoneNumber(event) {
        const input = event.target.value;
        this.phoneNumber = input.replace(/[^0-9]/g, '');
      },
      bookTimeSlot() {
        if (!this.fullName.trim() || !this.phoneNumber.trim() || !this.email.trim()) {
          this.bookingMessage = "All contact information fields are required.";
          return;
        }
  
        this.isBooking = true;
        this.bookingMessage = "Waiting for response...";
  
        const { timeSlotId, siteId } = this.timeSlot;
        const contactInformation = {
          fullName: this.fullName,
          phoneNumber: this.phoneNumber,
          email: this.email
        }
  
        fetch(
          `http://localhost:8080/tire-exchange/book-time?siteId=${siteId}&bookId=${timeSlotId}`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(contactInformation)
          }
        )
        .then((response) => response.json())
        .then((data) => {
          this.bookingMessage = data.statusMessage;
          if (data.statusCode === 200) {
            this.fadeOut = true;
            this.$emit('booked', { ...this.timeSlot, contactInformation });
          }
        })
        .catch((error) => {
          this.bookingMessage = "An error occurred while booking the time.";
          console.error("Error booking time slot:", error);
          this.isBooking = false;
        });
      },
      handleMouseDown() {
        this.isClicked = true;
      },
      handleMouseUp() {
        if (this.isClicked) {
          setTimeout(() => {
            this.isClicked = false;
          }, 500); // Duration of the click effect
        }
      }
    },
  };
  </script>
  
  <style scoped>
  .time-slot {
    border: 1px solid #ddd;
    padding: 10px;
    margin-bottom: 10px;
    transition: opacity 2s ease, background-color 0.3s ease;
  }
  
  .static-info {
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .static-info:hover {
    background-color: #f0f0f0;
  }
  
  .static-info:active {
    background-color: #e0e0e0;
  }
  
  .slot-details {
    margin-top: 10px;
  }
  
  .input-container {
    position: relative;
    margin-bottom: 10px;
  }
  
  .contact-input {
    width: 100%;
    padding: 8px;
    padding-right: 55px; /* Space for the character counter */
    box-sizing: border-box;
  }
  
  .char-counter {
    position: absolute;
    top: 50%;
    right: 10px;
    transform: translateY(-50%);
    font-size: 12px;
    color: #666;
    pointer-events: none; /* Ensure the counter doesn't interfere with input */
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
  
  .fade-out {
    opacity: 0;
  }
  </style>
  