function initializeCarousels() {
    const containers = document.querySelectorAll('.carousel-container');

    containers.forEach(container => {
        const images = container.getAttribute('data-images').split(',');
        const imgElement = container.querySelector('.carousel-image');
        const prevBtn = container.querySelector('.prev-btn');
        const nextBtn = container.querySelector('.next-btn');

        let currentIndex = 0;

        // Next Button Logic
        nextBtn.addEventListener('click', (e) => {
            e.preventDefault(); // Prevents page reload
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