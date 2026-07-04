const API_URL = 'http://localhost:8080/api/products';

async function fetchProducts() {
    try {
        console.log("Attempting to connect to backend at:", API_URL);
        
        // 1. Declare 'response' here so it is available in the whole function
        const response = await fetch(API_URL);
        
        // 2. Check if the response was successful
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // 3. Wait for the JSON data
        const products = await response.json();
        console.log("Successfully fetched products:", products);
        return products;
        
    } catch (error) {
        console.error("Could not fetch products:", error);
        // Returning null allows us to handle the error in renderProducts
        return null; 
    }
}


function renderProducts(products) {
    const grid = document.getElementById('product-grid');
    grid.innerHTML = ''; 

    if (products.length === 0) {
        grid.innerHTML = '<p>No products available at the moment.</p>';
        return;
    }

    products.forEach(product => {
        const card = document.createElement('div');
        card.className = 'product-card';

        const isAvailable = product.stockQuantity > 0;
        
        // Define the button or label based on stock
        const actionElement = isAvailable 
            ? `<a href="#" class="add-to-cart-btn" data-id="${product.id}">ADD TO CART</a>`
            : `<div class="sold-out-label">SOLD OUT</div>`;

        const imageUrls = product.imageUrls || [];
        const firstImage = imageUrls.length > 0 ? imageUrls[0] : 'images/default.jpg';

        card.innerHTML = `
            <div class="carousel-container" data-images="${imageUrls.join(',')}">
                <img class="carousel-image" src="${firstImage}" alt="${product.name}">
                <button class="carousel-btn prev-btn"><i class="ph ph-arrow-left"></i></button>
                <button class="carousel-btn next-btn"><i class="ph ph-arrow-right"></i></button>
            </div>
            <div class="product-name">${product.name}</div>
            <div class="product-category">${product.category}</div>
            <div class="product-price">${product.price.toFixed(2)} EGP</div>
            ${actionElement}
        `;

        grid.appendChild(card);
    });
}



async function initStore() {
    const products = await fetchProducts();
    renderProducts(products);
    
    // Once everything is rendered, initialize your carousel buttons
    if (typeof initializeCarousels === 'function') {
        initializeCarousels();
    }
}

// Run the script when the page is fully loaded
document.addEventListener('DOMContentLoaded', initStore);
