<template>
  <div id="app">
    <header>
      <h1>Tire Exchange Application</h1>
      <h2>Time Slot Booking</h2>
    </header>
    <main>
      <HomePage />
    </main>
    <footer>
      <p>© 2024 Tire Exchange</p>
    </footer>
  </div>
</template>

<script>
import HomePage from './views/HomePage.vue';

export default {
  name: 'App',
  components: {
    HomePage
  }
};
</script>

<style>
/* Global Styles */
html, body {
  height: 100%;
  margin: 0;
  font-family: 'Roboto', sans-serif; /* Apply the chosen font */
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* Ensures that the app takes at least the full height of the viewport */
}

header {
  background-color: #333;
  color: white;
  padding: 15px 20px;
  text-align: center;
}

header h1 {
  font-size: 2.5rem; /* Responsive font size */
  margin: 0;
}

header h2 {
  font-size: 1.5rem; /* Responsive font size */
  margin: 0;
}

main {
  flex: 1; /* Allows the main content area to grow and push the footer to the bottom */
  display: flex;
  justify-content: center;
  padding: 20px;
}

footer {
  background-color: #333;
  color: white;
  padding: 10px 20px;
  text-align: center;
  font-size: 0.875rem; /* Responsive font size */
}

footer p {
  margin: 0;
}

/* Responsive Font Sizes */
@media (max-width: 768px) {
  header h1 {
    font-size: 2rem;
  }

  header h2 {
    font-size: 1.25rem;
  }

  footer {
    font-size: 0.75rem;
  }
}

@media (max-width: 480px) {
  header h1 {
    font-size: 1.5rem;
  }

  header h2 {
    font-size: 1rem;
  }

  footer {
    font-size: 0.65rem;
  }
}
</style>
