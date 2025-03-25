const accordionContent = document.querySelectorAll(".accordion-content");

accordionContent.forEach((item, index) => {
  let header = item.querySelector("header");
  header.addEventListener("click", () => {
    item.classList.toggle("is-open");

    let description = item.querySelector(".accordion-content-description");
    if (item.classList.contains("is-open")) {
      // Scrollheight property return the height of
      // an element including padding
      description.style.height = `${description.scrollHeight}px`;
      item.querySelector("i").classList.replace("fa-plus", "fa-minus");
    } else {
      description.style.height = "0px";
      item.querySelector("i").classList.replace("fa-minus", "fa-plus");
    }
    // function to pass the index number of clicked header
    removeOpenedContent(index);
  })
})

function removeOpenedContent(index) {
  accordionContent.forEach((item2, index2) => {
    if (index != index2) {
      item2.classList.remove("is-open");
      let descrip = item2.querySelector(".accordion-content-description");
      descrip.style.height = "0px";
      item2.querySelector("i").classList.replace("fa-minus", "fa-plus");
    }
  })
}


const openButton = document.getElementById('open-sidebar-button')
const navbar = document.getElementById('navbar')

const media = window.matchMedia("(width < 100x)")

media.addEventListener('change', (e) => updateNavbar(e))

function updateNavbar(e) {
  const isMobile = e.matches
  console.log(isMobile)
  if (isMobile) {
    navbar.setAttribute('inert', '')
  }
  else {
    // desktop device
    navbar.removeAttribute('inert')
  }
}

function openSidebar() {
  navbar.classList.add('show')
  openButton.setAttribute('aria-expanded', 'true')
  navbar.removeAttribute('inert')
}

function closeSidebar() {
  navbar.classList.remove('show')
  openButton.setAttribute('aria-expanded', 'false')
  navbar.setAttribute('inert', '')
}

updateNavbar(media)


const loadingHTML = ` 
        <div class="container_test">
        <div class="avatar skeleton"></div>
        <div class="title-skeleton skeleton"></div>
        <div class="grid-skeleton">
          <div class="grid-item-skeleton">
            <div class="label-skeleton skeleton"></div>
            <div class="value skeleton"></div>
          </div>
          <div class="grid-item-skeleton">
            <div class="label skeleton"></div>
            <div class="value skeleton"></div>
          </div>
          <div class="grid-item-skeleton">
            <div class="label-skeleton skeleton"></div>
            <div class="value skeleton"></div>
          </div>
          <div class="grid-item-skeleton">
            <div class="label-skeleton skeleton"></div>
            <div class="value skeleton"></div>
          </div>
          <div class="grid-item-skeleton">
            <div class="label-skeleton skeleton"></div>
            <div class="value skeleton"></div>
          </div>
          <div class="grid-item-skeleton">
            <div class="label-skeleton skeleton"></div>
            <div class="value skeleton"></div>
          </div>
        </div>
        <div class="buttons-skeleton">
          <div class="button-skeleton skeleton"></div>
          <div class="button-skeleton skeleton"></div>
        </div>
      </div>`
let data_final = [];
let data_spread = [];
async function getCalendarData() {
  let grid_container = document.querySelector(".grid-container")
  grid_container.innerHTML = grid_container.innerHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML
  let a = await fetch("http://127.0.0.1:8000/api/ipo-calendar/")
  let jsonResponse = await a.json();
  data_final = jsonResponse.results;

  grid_container.innerHTML = ``
  for (const datas of jsonResponse.results) {
    grid_container.innerHTML = grid_container.innerHTML +
      `
                  <div class="container_test">
            <div class="flex-container-2">
              ${datas.symbol}
              <div class="title-container">
                <h2 class="title-text">${datas.company_name}</h2>
              </div>
    
            </div>
            <div class="card-grid-container">
              <div class="price-container">
                <p class="price-label">PRICE BAND</p>
                <p class="price-value"><strong>${datas.ipo_price == "None-None" ? "Not issued" : datas.currency + " " + datas.ipo_price}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">OPEN</p>
                <p class="price-value"><strong>${datas.opening_date ? datas.opening_date : "Not issued"}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">CLOSE</p>
                <p class="price-value"><strong>${datas.close_date == "N/A" ? "Not issued" : datas.close_date}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">ISSUE SIZE</p>
                <p class="price-value"><strong>${datas.issue_size ? formatAmount(datas.issue_size) : "Not issued"}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">ISSUE TYPE</p>
                <p class="price-value"><strong>${datas.issue_type ? datas.issue_type : "Not issued"}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">LISTING DATE</p>
                <p class="price-value"><strong>${datas.listing_date == "N/A" ? "Not issued" : datas.listing_date}</strong></p>
              </div>
    
            </div>
            <div class="button-container">
              <button class="btn rhp-btn">RHP</button>
              <button class="btn drhp-btn">DRHP</button>
            </div>
          </div>
            `
  }
  console.log(jsonResponse.results);
  console.log("loading ended")

}
getCalendarData()

function formatAmount(amount) {
  if (amount >= 10000000) {
    return (amount / 10000000).toFixed(2) + 'Cr'; // For Crores
  } else if (amount >= 100000) {
    return (amount / 100000).toFixed(2) + 'L'; // For Lakhs
  } else {
    return amount.toString(); // For smaller amounts, return as-is
  }
}


const fetchData = async (page) => {
  try {
    let grid_container = document.querySelector(".grid-container")
    grid_container.innerHTML = "";

    grid_container.innerHTML = grid_container.innerHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML

    const response = await fetch(`http://127.0.0.1:8000/api/ipo-calendar?page=${page}`);
    const data = await response.json();
    data_final = data.results;
    return data.results;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

const renderData = (data) => {
  let grid_container = document.querySelector(".grid-container")
  grid_container.innerHTML = "";
  for (const datas of data) {
    grid_container.innerHTML = grid_container.innerHTML +
      `
                  <div class="container_test">
            <div class="flex-container-2">
              ${datas.symbol}
              <div class="title-container">
                <h2 class="title-text">${datas.company_name}</h2>
              </div>
    
            </div>
            <div class="card-grid-container">
              <div class="price-container">
                <p class="price-label">PRICE BAND</p>
                <p class="price-value"><strong>${datas.ipo_price == "None-None" ? "Not issued" : datas.currency + " " + datas.ipo_price}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">OPEN</p>
                <p class="price-value"><strong>${datas.opening_date ? datas.opening_date : "Not issued"}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">CLOSE</p>
                <p class="price-value"><strong>${datas.close_date == "N/A" ? "Not issued" : datas.close_date}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">ISSUE SIZE</p>
                <p class="price-value"><strong>${datas.issue_size ? formatAmount(datas.issue_size) : "Not issued"}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">ISSUE TYPE</p>
                <p class="price-value"><strong>${datas.issue_type ? datas.issue_type : "Not issued"}</strong></p>
              </div>
              <div class="price-container">
                <p class="price-label">LISTING DATE</p>
                <p class="price-value"><strong>${datas.listing_date == "N/A" ? "Not issued" : datas.listing_date}</strong></p>
              </div>
    
            </div>
            <div class="button-container">
              <button class="btn rhp-btn">RHP</button>
              <button class="btn drhp-btn">DRHP</button>
            </div>
          </div>
            `
  }
};

let currentPage = 1;

const loadMoreData = async () => {
  currentPage++;
  let grid_container = document.querySelector(".grid-container")
  grid_container.innerHTML = "";

  grid_container.innerHTML = grid_container.innerHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML + loadingHTML

  const data = await fetchData(currentPage)
  console.log(data)
  renderData(data);
};
document.getElementById('load-more').addEventListener('click', loadMoreData);

// data sorting functionality
function sortByListingDate() {
  const filteredData = data_final.filter(
    (item) => item.opening_date !== "N/A" && item.opening_date
  );
  filteredData.sort((a, b) => {
    const dateA = new Date(a.opening_date);
    const dateB = new Date(b.opening_date);
    return dateA - dateB; // Ascending order
  });
  console.log("Sorted Data:", filteredData);
  data_spread = filteredData;
  renderData(data_spread)
}
document
  .getElementById("Filter_listing")
  .addEventListener("click", sortByListingDate);

function filterByCompanyName(event) {
  const searchTerm = event.target.value.toLowerCase(); // Get input value and convert to lowercase
  const filteredData = data_final.filter((item) =>
    item.company_name.toLowerCase().includes(searchTerm) // Check if company_name contains the search term
  );
  renderData(filteredData)
  console.log("Filtered Data:", filteredData);
}
document
  .getElementById("search-bar")
  .addEventListener("input", filterByCompanyName);
