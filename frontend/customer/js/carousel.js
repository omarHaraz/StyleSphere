function initializeCarousels() {
    const containers = document.querySelectorAll('.carousel-container');

    containers.forEach(container => {
        const dataImagesAttr = container.getAttribute('data-images');
        // Handle empty arrays gracefully
        const images = dataImagesAttr ? dataImagesAttr.split(',') : [];
        
        const imgElement = container.querySelector('.carousel-image');
        const prevBtn = container.querySelector('.prev-btn');
        const nextBtn = container.querySelector('.next-btn');

        // If there is only 1 image (or none), hide the navigation arrows!
        if (images.length <= 1) {
            if (prevBtn) prevBtn.style.display = 'none';
            if (nextBtn) nextBtn.style.display = 'none';
            return; // Skip adding event listeners since we don't need them
        }

        let currentIndex = 0;

        // Next Button Logic
        nextBtn.addEventListener('click', (e) => {
            e.preventDefault(); // Prevents page reload or anchor jumps
            currentIndex = (currentIndex + 1) % images.length;
            imgElement.src = images[currentIndex];
        });

        // Previous Button Logic
        prevBtn.addEventListener('click', (e) => {
            e.preventDefault();
            currentIndex = (currentIndex - 1 + images.length) % images.length;
            imgElement.src = images[currentIndex];
        });
    });
}