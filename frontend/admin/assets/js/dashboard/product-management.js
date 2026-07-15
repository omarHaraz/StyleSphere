// ======================================================
// PRODUCT MANAGEMENT
// ======================================================

const PRODUCT_API = "http://localhost:8080/api/admin/products";
const CATEGORY_API = "http://localhost:8080/api/admin/categories";

const user = JSON.parse(localStorage.getItem("user"));

if (!user || !user.token) {
    window.location.href = "../../customer/login.html";
}

const AUTH_HEADERS = {
    Authorization: `Bearer ${user.token}`
};

let products = [];
let categories = [];

let modal;

// ======================================================
// PAGE LOAD
// ======================================================

document.addEventListener("DOMContentLoaded", async () => {

    modal = new bootstrap.Modal(
        document.getElementById("productModal")
    );

    document
        .getElementById("productForm")
        .addEventListener("submit", saveProduct);

    // This handles the image previews correctly without duplicating
    document
        .getElementById("productImages")
        .addEventListener("change", previewImages);

    document
        .getElementById("newCategory")
        .addEventListener("change", createCategoryIfNeeded);

    await loadCategories();

    await loadProducts();

});

// ======================================================
// CUSTOM MODAL ALERT/CONFIRMATION FUNCTIONS
// ======================================================

/**
 * Replaces the native browser confirm() dialog with a stunning dashboard modal.
 */
function showConfirm(title, message, confirmBtnText = "Delete") {
    return new Promise((resolve) => {
        const confirmModalEl = document.getElementById("confirmModal");
        const confirmModal = new bootstrap.Modal(confirmModalEl);
        
        document.getElementById("confirmTitle").innerText = title;
        document.getElementById("confirmMessage").innerText = message;
        
        const confirmBtn = document.getElementById("confirmBtn");
        confirmBtn.innerText = confirmBtnText;
        
        // Prevent event listener accumulation by cloning button
        const newConfirmBtn = confirmBtn.cloneNode(true);
        confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
        
        newConfirmBtn.addEventListener("click", () => {
            confirmModal.hide();
            resolve(true);
        });
        
        // If they close the modal without clicking yes
        confirmModalEl.addEventListener('hidden.bs.modal', () => {
            resolve(false);
        }, { once: true });
        
        confirmModal.show();
    });
}

/**
 * Replaces the native alert() popup with an awesome Material Status modal.
 */
function showAlert(title, message, type = "success") {
    const statusModalEl = document.getElementById("statusModal");
    const statusModal = new bootstrap.Modal(statusModalEl);
    
    document.getElementById("statusTitle").innerText = title;
    document.getElementById("statusMessage").innerText = message;
    
    const iconBg = document.getElementById("statusIconBg");
    const icon = document.getElementById("statusIcon");
    
    if (type === "success") {
        iconBg.className = "mx-auto mb-3 d-flex align-items-center justify-content-center bg-gradient-success shadow-success";
        icon.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="white" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
            </svg>
        `;
    } else {
        iconBg.className = "mx-auto mb-3 d-flex align-items-center justify-content-center bg-gradient-danger shadow-danger";
        icon.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="white" viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
        `;
    }
    
    statusModal.show();
}

// ======================================================
// LOAD PRODUCTS
// ======================================================

async function loadProducts() {
    try {
        const response = await fetch(PRODUCT_API, {
            method: "GET",
            headers: AUTH_HEADERS
        });

        if (!response.ok)
            throw new Error("Unable to load products.");

        products = await response.json();
        renderProducts();
    }
    catch (error) {
        console.error(error);
        showAlert("Error", "Failed to load products from server.", "error");
    }
}

// ======================================================
// SAVE PRODUCT WITH CLOUD UPLOAD INDICATOR
// ======================================================

async function saveProduct(event) {
    if (event) {
        event.preventDefault();
    }

    const submitButton = document.querySelector("#productForm button[type='submit']");
    
    const form = document.getElementById("productForm");
    const productId = document.getElementById("productId").value.trim();
    const productName = document.getElementById("productName").value.trim();
    const description = document.getElementById("description").value.trim();
    const price = Number(document.getElementById("price").value);
    const stockQuantity = Number(document.getElementById("stockQuantity").value);
    
    const categorySelect = document.getElementById("categorySelect");
    const categoryId = categorySelect.value;
    const categoryName = categorySelect.options[categorySelect.selectedIndex]?.text?.trim();

    if (!productName || !description) {
        showAlert("Wait!", "Please fill in the product name and description.", "error");
        return;
    }

    if (!Number.isFinite(price) || !Number.isFinite(stockQuantity)) {
        showAlert("Invalid Input", "Please enter valid price and stock quantity values.", "error");
        return;
    }

    const originalButtonHTML = submitButton.innerHTML;

    try {
        if (submitButton) {
            submitButton.disabled = true;
            
            const filesSelected = document.getElementById("productImages")?.files?.length > 0;
            
            if (filesSelected) {
                submitButton.innerHTML = `
                    <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Uploading to photos...
                `;
            } else {
                submitButton.innerHTML = `
                    <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Saving...
                `;
            }
        }

        const formData = new FormData(form);

        const fileInput = document.getElementById("productImages");
        if (!fileInput || fileInput.files.length === 0) {
            formData.delete("images"); 
        }

        if (categoryId) {
            formData.set("categoryId", categoryId);
        }
        if (categoryName) {
            formData.set("categoryName", categoryName);
        }

        formData.set("name", productName);
        formData.set("description", description);
        formData.set("price", String(price));
        formData.set("stockQuantity", String(stockQuantity));

        const url = productId ? `${PRODUCT_API}/${productId}` : PRODUCT_API;

        const response = await fetch(url, {
            method: productId ? "PUT" : "POST",
            headers: {
                Authorization: AUTH_HEADERS.Authorization
            },
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Unable to save product.");
        }

        resetProductForm();
        modal.hide();
        await loadProducts();

        showAlert("Success!", "Product saved successfully.", "success");
    }
    catch (error) {
        console.error(error);
        showAlert("Failed", error.message || "Failed to save product.", "error");
    }
    finally {
        if (submitButton) {
            submitButton.disabled = false;
            submitButton.innerHTML = originalButtonHTML; 
        }
    }
}

// ======================================================
// RENDER TABLE (UPDATED: IMAGES & STATUS DELETED)
// ======================================================

function renderProducts() {
    const tbody = document.getElementById("products-table-body");
    tbody.innerHTML = "";

    products.forEach(product => {
        const displayCategory = product.categoryName || product.category || "-";

        const row = `
            <tr>
                <td><strong>${product.name}</strong></td>
                <td>${displayCategory}</td>
                <td>$${Number(product.price).toFixed(2)}</td>
                <td>${product.stockQuantity}</td>
                <td>
                    <button class="btn btn-link text-dark px-2" onclick="openEditModal(${product.id})">Edit</button>
                    <button class="btn btn-link text-danger px-2" onclick="deleteProduct(${product.id})">Delete</button>
                </td>
            </tr>
        `;
        tbody.insertAdjacentHTML("beforeend", row);
    });
}

// ======================================================
// DELETE PRODUCT (PERMANENT HARD DELETE)
// ======================================================

async function deleteProduct(id) {
    const isConfirmed = await showConfirm(
        "Delete Product?", 
        "Are you sure you want to permanently delete this product? All image reference records and Cloudinary CDN assets will be wiped out completely."
    );

    if (!isConfirmed) return;

    try {
        const response = await fetch(`${PRODUCT_API}/${id}`, {
            method: "DELETE",
            headers: AUTH_HEADERS
        });

        if (!response.ok) {
            throw new Error("Unable to delete product.");
        }

        await loadProducts();
        showAlert("Deleted!", "Product deleted successfully.", "success");
    }
    catch (error) {
        console.error(error);
        showAlert("Error", "Failed to delete product.", "error");
    }
}

// ======================================================
// LOAD CATEGORIES
// ======================================================

async function loadCategories() {
    try {
        const response = await fetch(CATEGORY_API, {
            method: "GET",
            headers: AUTH_HEADERS
        });

        if (!response.ok) throw new Error();

        categories = await response.json();
        const select = document.getElementById("categorySelect");
        select.innerHTML = "";

        categories.forEach(category => {
            select.innerHTML += `
                <option value="${category.id}">${category.name}</option>
            `;
        });
    }
    catch (error) {
        console.error(error);
    }
}

// ======================================================
// OPEN CREATE MODAL
// ======================================================

function openCreateModal() {
    document.getElementById("productForm").reset();
    document.getElementById("productId").value = "";
    document.getElementById("imagePreview").innerHTML = "";
    document.getElementById("currentImages").innerHTML = "";
    document.getElementById("productModalLabel").innerText = "Add Product";
    modal.show();
}

// ======================================================
// IMAGE PREVIEW
// ======================================================

function previewImages() {
    const fileInput = document.getElementById("productImages");
    const preview = document.getElementById("imagePreview");

    preview.innerHTML = "";
    const files = fileInput.files;

    if (files.length > 10) {
        showAlert("Oops!", "Maximum 10 images allowed.", "error");
        fileInput.value = ""; 
        return;
    }

    [...files].forEach(file => {
        const reader = new FileReader();

        reader.onload = function(e) {
            preview.innerHTML += `
                <div class="col-md-3">
                    <div class="card">
                        <img
                            src="${e.target.result}"
                            class="card-img-top"
                            style="height:180px;object-fit:cover;">
                    </div>
                </div>
            `;
        };
        reader.readAsDataURL(file);
    });
}

// ======================================================
// OPEN EDIT MODAL
// ======================================================

async function openEditModal(id) {
    try {
        const response = await fetch(`${PRODUCT_API}/${id}`, {
            method: "GET",
            headers: AUTH_HEADERS
        });

        if (!response.ok) throw new Error("Unable to load product.");

        const product = await response.json();

        document.getElementById("productModalLabel").innerText = "Edit Product";
        document.getElementById("productId").value = product.id;
        document.getElementById("productName").value = product.name;
        document.getElementById("description").value = product.description;
        document.getElementById("price").value = product.price;
        document.getElementById("stockQuantity").value = product.stockQuantity;

        const categorySelect = document.getElementById("categorySelect");
        if (product.categoryId != null) {
            categorySelect.value = product.categoryId;
        }
        if (product.category && product.category.id) {
            categorySelect.value = product.category.id;
        }

        document.getElementById("imagePreview").innerHTML = "";
        const currentImages = document.getElementById("currentImages");
        currentImages.innerHTML = "";

        if (product.imageUrls && product.imageUrls.length > 0) {
            product.imageUrls.forEach(image => {
                currentImages.innerHTML += `
                    <div class="col-md-3 mb-3">
                        <div class="card shadow-sm">
                            <img src="${image}" class="card-img-top" style="height:180px;object-fit:cover;">
                        </div>
                    </div>
                `;
            });
        } else {
            currentImages.innerHTML = `<div class="text-center text-muted">No Images Uploaded</div>`;
        }

        modal.show();
    }
    catch (error) {
        console.error(error);
        showAlert("Error", "Failed to load product details.", "error");
    }
}

// ======================================================
// RESET FORM
// ======================================================

function resetProductForm() {
    document.getElementById("productForm").reset();
    document.getElementById("productId").value = "";
    document.getElementById("imagePreview").innerHTML = "";
    document.getElementById("currentImages").innerHTML = "";
    document.getElementById("newCategory").value = "";
}

// ======================================================
// CREATE CATEGORY (Optional)
// ======================================================

async function createCategoryIfNeeded() {
    const categoryName = document.getElementById("newCategory").value.trim();

    if (categoryName === "") return;

    try {
        const response = await fetch(CATEGORY_API, {
            method: "POST",
            headers: {
                ...AUTH_HEADERS,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ name: categoryName })
        });

        if (!response.ok) throw new Error();

        const newCategory = await response.json();
        await loadCategories();

        document.getElementById("categorySelect").value = newCategory.id;
        document.getElementById("newCategory").value = "";
        showAlert("Awesome!", "Category created successfully.", "success");
    }
    catch (error) {
        console.error(error);
        showAlert("Failed", "Unable to create category.", "error");
    }
}