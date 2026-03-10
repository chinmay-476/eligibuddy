const ELIGIBILITY_DATA_URL = '/data/eligibility-data.json';

let eligibilityDatabase = null;
let eligibilityDatabasePromise = null;
let currentResults = null;
let currentFilter = 'all';

function getCurrentUsername() {
    return (document.body?.dataset.username || '').trim();
}

function isUserAuthenticated() {
    const username = getCurrentUsername();
    return username !== '' && username !== 'null' && username !== 'Guest';
}

function delay(ms) {
    return new Promise((resolve) => window.setTimeout(resolve, ms));
}

async function loadEligibilityDatabase() {
    const response = await fetch(ELIGIBILITY_DATA_URL, { cache: 'no-store' });
    if (!response.ok) {
        throw new Error(`Failed to load eligibility data (${response.status})`);
    }

    eligibilityDatabase = await response.json();
    return eligibilityDatabase;
}

function ensureEligibilityDatabaseLoaded() {
    if (eligibilityDatabase) {
        return Promise.resolve(eligibilityDatabase);
    }

    if (!eligibilityDatabasePromise) {
        eligibilityDatabasePromise = loadEligibilityDatabase().catch((error) => {
            eligibilityDatabasePromise = null;
            throw error;
        });
    }

    return eligibilityDatabasePromise;
}

function preloadEligibilityDatabase() {
    ensureEligibilityDatabaseLoaded().catch((error) => {
        console.error('Failed to preload eligibility data:', error);
    });
}

// Authentication check function
        async function checkAuthentication() {
            // Check if user is authenticated using Thymeleaf variables
            const isAuthenticated = isUserAuthenticated();
            
            if (!isAuthenticated) {
                alert('Please login to access the eligibility analysis feature!');
                window.location.href = '/login';
                return;
            }

            const form = document.getElementById('eligibilityForm');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            showLoading();

            try {
                await ensureEligibilityDatabaseLoaded();
                await delay(600);
                checkEligibility();
            } catch (error) {
                console.error('Error preparing eligibility data:', error);
                alert('Opportunity data could not be loaded. Please refresh and try again.');
            } finally {
                hideLoading();
            }
        }

        // Enhanced form submission with loading and animations
        document.getElementById('eligibilityForm').addEventListener('submit', function(e) {
            e.preventDefault();
            checkAuthentication();
        });

        function showLoading() {
            document.getElementById('loadingOverlay').style.display = 'flex';
            document.getElementById('submitBtn').disabled = true;
        }

        function hideLoading() {
            document.getElementById('loadingOverlay').style.display = 'none';
            document.getElementById('submitBtn').disabled = false;
        }

        function checkEligibility() {
            try {
                const formData = new FormData(document.getElementById('eligibilityForm'));
                const userData = {
                    firstName: formData.get('firstName') || '',
                    lastName: formData.get('lastName') || '',
                    fullName: (formData.get('firstName') || '') + ' ' + (formData.get('lastName') || ''),
                    age: parseInt(formData.get('age')) || 0,
                    gender: formData.get('gender') || '',
                    state: formData.get('state') || '',
                    district: formData.get('district') || '',
                    qualification: formData.get('qualification') || '',
                    category: formData.get('category') || '',
                    income: formData.get('income') || '',
                    email: formData.get('email') || '',
                    field: formData.get('field') || '',
                    disability: formData.get('disability') || 'No',
                    preferences: Array.from(formData.getAll('preferences'))
                };

                // Validate required fields
                if (!userData.firstName || !userData.lastName || !userData.age || !userData.gender || 
                    !userData.state || !userData.qualification || !userData.category || !userData.income) {
                    alert('Please fill in all required fields!');
                    return;
                }

                const results = {
                    scholarships: [],
                    schemes: [],
                    exams: [],
                    jobs: []
                };

                // Check eligibility for each category based on user preferences
                userData.preferences.forEach(preference => {
                    if (eligibilityDatabase[preference]) {
                        eligibilityDatabase[preference].forEach(item => {
                            if (isEligible(userData, item.eligibility)) {
                                results[preference].push(item);
                            }
                        });
                    }
                });

                currentResults = { results, userData };
                displayResults(results, userData);
                
                // Show success message
                const successMessage = document.getElementById('successMessage');
                if (successMessage) {
                    successMessage.classList.add('show');
                    setTimeout(() => {
                        successMessage.classList.remove('show');
                    }, 5000);
                }
            } catch (error) {
                console.error('Error checking eligibility:', error);
                alert('An error occurred while checking eligibility. Please try again.');
                hideLoading();
            }
        }

        function isEligible(userData, eligibility) {
            try {
                // Check age eligibility with category-based relaxation
                if (eligibility.age && Array.isArray(eligibility.age) && eligibility.age.length === 2) {
                    const [minAge, maxAge] = eligibility.age;
                    let adjustedMaxAge = maxAge;
                    
                    // Apply age relaxation for reserved categories
                    if (eligibility.ageRelaxation && userData.category) {
                        const relaxation = eligibility.ageRelaxation[userData.category];
                        if (relaxation) {
                            adjustedMaxAge += relaxation;
                        }
                    }
                    
                    if (userData.age < minAge || userData.age > adjustedMaxAge) {
                        return false;
                    }
                }

                // Check gender eligibility
                if (eligibility.gender && Array.isArray(eligibility.gender) && !eligibility.gender.includes(userData.gender)) {
                    return false;
                }

                // Check qualification eligibility
                if (eligibility.qualification && Array.isArray(eligibility.qualification) && !eligibility.qualification.includes(userData.qualification)) {
                    return false;
                }

                // Check category eligibility
                if (eligibility.category && Array.isArray(eligibility.category) && !eligibility.category.includes(userData.category)) {
                    return false;
                }

                // Check income eligibility
                if (eligibility.income && Array.isArray(eligibility.income) && !eligibility.income.includes(userData.income)) {
                    return false;
                }

                // Check field eligibility
                if (eligibility.field && Array.isArray(eligibility.field) && userData.field && !eligibility.field.includes(userData.field)) {
                    return false;
                }

                return true;
            } catch (error) {
                console.error('Error checking eligibility:', error);
                return false;
            }
        }

        function displayResults(results, userData) {
            try {
                const resultsSection = document.getElementById('resultsSection');
                if (!resultsSection) {
                    console.error('Results section not found');
                    return;
                }
                
                // Update statistics with animation
                animateCountUp('scholarshipCount', results.scholarships.length);
                animateCountUp('schemeCount', results.schemes.length);
                animateCountUp('examCount', results.exams.length);
                animateCountUp('jobCount', results.jobs.length);

                // Show results section
                resultsSection.classList.add('show');
                resultsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });

                // Generate and display results
                generateResultsHTML(results);
            } catch (error) {
                console.error('Error displaying results:', error);
                alert('An error occurred while displaying results. Please try again.');
            }
        }

        function animateCountUp(elementId, targetValue) {
            try {
                const element = document.getElementById(elementId);
                if (!element) {
                    console.error(`Element with id '${elementId}' not found`);
                    return;
                }
                
                let currentValue = 0;
                const increment = targetValue / 20;
                const timer = setInterval(() => {
                    currentValue += increment;
                    if (currentValue >= targetValue) {
                        currentValue = targetValue;
                        clearInterval(timer);
                    }
                    element.textContent = Math.floor(currentValue);
                }, 50);
            } catch (error) {
                console.error('Error in count up animation:', error);
            }
        }

        function generateResultsHTML(results) {
            try {
                const resultsGrid = document.getElementById('resultsGrid');
                if (!resultsGrid) {
                    console.error('Results grid not found');
                    return;
                }
                
                let resultsHTML = '';

                const categoryInfo = {
                    scholarships: { 
                        icon: 'fas fa-graduation-cap', 
                        title: 'Scholarships',
                        color: '#667eea'
                    },
                    schemes: { 
                        icon: 'fas fa-university', 
                        title: 'Government Schemes',
                        color: '#28a745'
                    },
                    exams: { 
                        icon: 'fas fa-file-alt', 
                        title: 'Competitive Exams',
                        color: '#ffc107'
                    },
                    jobs: { 
                        icon: 'fas fa-briefcase', 
                        title: 'Government Jobs',
                        color: '#17a2b8'
                    }
                };

                Object.keys(results).forEach(category => {
                    if (results[category] && Array.isArray(results[category]) && results[category].length > 0) {
                        resultsHTML += `
                            <div class="category-section" data-category="${category}">
                                <div class="category-header">
                                    <i class="category-icon ${categoryInfo[category]?.icon || 'fas fa-info-circle'}"></i>
                                    <div class="category-title">${categoryInfo[category]?.title || category}</div>
                                </div>
                        `;

                        results[category].forEach(item => {
                            resultsHTML += createOpportunityCard(item, category);
                        });

                        resultsHTML += '</div>';
                    }
                });

                if (resultsHTML === '') {
                    resultsHTML = `
                        <div class="no-results">
                            <i class="no-results-icon fas fa-search"></i>
                            <h3>No matching opportunities found</h3>
                            <p>Try adjusting your criteria or enabling more preferences in the advanced options.</p>
                        </div>
                    `;
                }

                resultsGrid.innerHTML = resultsHTML;
            } catch (error) {
                console.error('Error generating results HTML:', error);
                const resultsGrid = document.getElementById('resultsGrid');
                if (resultsGrid) {
                    resultsGrid.innerHTML = `
                        <div class="no-results">
                            <i class="no-results-icon fas fa-exclamation-triangle"></i>
                            <h3>Error displaying results</h3>
                            <p>An error occurred while generating the results. Please try again.</p>
                        </div>
                    `;
                }
            }
        }

        function createOpportunityCard(item, category) {
            try {
                if (!item || !item.name) {
                    console.error('Invalid item data:', item);
                    return '';
                }
                
                const details = getItemDetails(item, category);
                const itemName = item.name.replace(/'/g, "\\'").replace(/"/g, '\\"');
                
                return `
                    <div class="opportunity-card">
                        <div class="opportunity-header">
                            <div>
                                <div class="opportunity-title">${item.name || 'Unknown Opportunity'}</div>
                            </div>
                            <div class="opportunity-badge">${item.type || 'Available'}</div>
                        </div>
                        <div class="opportunity-description">${item.description || 'No description available'}</div>
                        <div class="opportunity-details">
                            ${details}
                        </div>
                        <div class="opportunity-actions">
                            <button class="btn-sm btn-outline" onclick="viewDetails('${itemName}', '${category}')">
                                <i class="fas fa-info-circle"></i> View Details
                            </button>
                            <button class="btn-sm btn-outline" onclick="applyNow('${itemName}', '${category}')">
                                <i class="fas fa-external-link-alt"></i> Apply Now
                            </button>
                        </div>
                    </div>
                `;
            } catch (error) {
                console.error('Error creating opportunity card:', error);
                return '';
            }
        }

        function getItemDetails(item, category) {
            try {
                if (!item) {
                    return '';
                }
                
                let details = '';
                
                switch(category) {
                    case 'scholarships':
                        details = `
                            <div class="detail-item">
                                <i class="fas fa-money-bill-wave"></i>
                                <span>${item.amount || 'Amount varies'}</span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-calendar-alt"></i>
                                <span>Deadline: ${item.deadline || 'Not specified'}</span>
                            </div>
                        `;
                        break;
                    case 'schemes':
                        details = `
                            <div class="detail-item">
                                <i class="fas fa-gift"></i>
                                <span>${item.benefit || 'Benefits vary'}</span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-clock"></i>
                                <span>${item.deadline || 'Ongoing'}</span>
                            </div>
                        `;
                        break;
                    case 'exams':
                        details = `
                            <div class="detail-item">
                                <i class="fas fa-calendar"></i>
                                <span>${item.examDate || 'Date TBA'}</span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-rupee-sign"></i>
                                <span>Fee: ${item.applicationFee || 'Not specified'}</span>
                            </div>
                        `;
                        break;
                    case 'jobs':
                        details = `
                            <div class="detail-item">
                                <i class="fas fa-rupee-sign"></i>
                                <span>${item.salary || 'Salary varies'}</span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-users"></i>
                                <span>${item.vacancies || 'Multiple'} positions</span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-calendar-check"></i>
                                <span>Apply by: ${item.applicationDeadline || 'Date TBA'}</span>
                            </div>
                        `;
                        break;
                    default:
                        details = `
                            <div class="detail-item">
                                <i class="fas fa-info-circle"></i>
                                <span>Details available</span>
                            </div>
                        `;
                }
                
                return details;
            } catch (error) {
                console.error('Error getting item details:', error);
                return '';
            }
        }

        // Filter functionality
        function filterResults(category) {
            currentFilter = category;
            
            // Update active filter button
            document.querySelectorAll('.filter-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            const activeButton = document.querySelector(`.filter-btn[data-filter="${category}"]`);
            if (activeButton) {
                activeButton.classList.add('active');
            }
            
            // Show/hide categories
            const categoryElements = document.querySelectorAll('.category-section');
            categoryElements.forEach(element => {
                if (category === 'all' || element.dataset.category === category) {
                    element.style.display = 'block';
                    element.style.animation = 'fadeInUp 0.5s ease-out';
                } else {
                    element.style.display = 'none';
                }
            });
        }

        // Advanced options toggle
        function toggleAdvanced() {
            const content = document.getElementById('advancedContent');
            const icon = document.getElementById('advancedIcon');
            
            if (content.classList.contains('show')) {
                content.classList.remove('show');
                icon.innerHTML = '<i class="fas fa-chevron-down"></i>';
            } else {
                content.classList.add('show');
                icon.innerHTML = '<i class="fas fa-chevron-up"></i>';
            }
        }

        // Action handlers
        function viewDetails(itemName, category) {
            // Find the item in the database
            const item = eligibilityDatabase[category].find(i => i.name === itemName);
            if (item) {
                // Create a modal or detailed view
                showDetailModal(item, category);
            }
        }

        function applyNow(itemName, category) {
            // In a real application, this would redirect to the application portal
            alert(`Redirecting to application portal for: ${itemName}`);
        }

        function showDetailModal(item, category) {
            // Create a detailed modal with all information
            const modalHTML = `
                <div class="modal-overlay" onclick="closeModal()">
                    <div class="modal-content" onclick="event.stopPropagation()">
                        <div class="modal-header">
                            <h2>${item.name}</h2>
                            <button class="modal-close" onclick="closeModal()">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                        <div class="modal-body">
                            <p><strong>Description:</strong> ${item.description}</p>
                            ${getDetailedInfo(item, category)}
                        </div>
                        <div class="modal-footer">
                            <button class="btn-primary" onclick="applyNow('${item.name}', '${category}')">
                                Apply Now
                            </button>
                            <button class="btn-white" onclick="closeModal()">
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            `;
            
            document.body.insertAdjacentHTML('beforeend', modalHTML);
        }

        function getDetailedInfo(item, category) {
            switch(category) {
                case 'scholarships':
                    return `
                        <p><strong>Amount:</strong> ${item.amount || 'Not specified'}</p>
                        <p><strong>Type:</strong> ${item.type || 'General'}</p>
                        <p><strong>Deadline:</strong> ${item.deadline || 'Not specified'}</p>
                        <p><strong>Eligibility:</strong></p>
                        <ul>
                            ${item.eligibility.qualification ? `<li>Qualification: ${item.eligibility.qualification.join(', ')}</li>` : ''}
                            ${item.eligibility.category ? `<li>Category: ${item.eligibility.category.join(', ')}</li>` : ''}
                            ${item.eligibility.income ? `<li>Income: ${item.eligibility.income.join(', ')}</li>` : ''}
                        </ul>
                    `;
                case 'schemes':
                    return `
                        <p><strong>Benefit:</strong> ${item.benefit || 'Not specified'}</p>
                        <p><strong>Type:</strong> ${item.type || 'General'}</p>
                        <p><strong>Status:</strong> ${item.deadline || 'Ongoing'}</p>
                    `;
                case 'exams':
                    return `
                        <p><strong>Exam Date:</strong> ${item.examDate || 'To be announced'}</p>
                        <p><strong>Application Fee:</strong> ${item.applicationFee || 'Not specified'}</p>
                        <p><strong>Type:</strong> ${item.type || 'General'}</p>
                    `;
                case 'jobs':
                    return `
                        <p><strong>Salary:</strong> ${item.salary || 'Not specified'}</p>
                        <p><strong>Vacancies:</strong> ${item.vacancies || 'Multiple'}</p>
                        <p><strong>Application Deadline:</strong> ${item.applicationDeadline || 'To be announced'}</p>
                        <p><strong>Type:</strong> ${item.type || 'Government'}</p>
                    `;
                default:
                    return '';
            }
        }

        function closeModal() {
            const modal = document.querySelector('.modal-overlay');
            if (modal) {
                modal.remove();
            }
        }

        // Download PDF functionality
        function downloadPDF() {
            if (!currentResults) {
                alert('Please check eligibility first!');
                return;
            }

            const { results, userData } = currentResults;
            
            // Create comprehensive report content
            let content = `ELIGIBILITY ANALYSIS REPORT\n`;
            content += `${'='.repeat(50)}\n\n`;
            content += `PERSONAL INFORMATION\n`;
            content += `${'─'.repeat(25)}\n`;
            content += `Name: ${userData.fullName}\n`;
            content += `Age: ${userData.age} years\n`;
            content += `Gender: ${userData.gender}\n`;
            content += `State: ${userData.state}\n`;
            if (userData.district) content += `District: ${userData.district}\n`;
            content += `Highest Qualification: ${userData.qualification}\n`;
            content += `Category: ${userData.category}\n`;
            content += `Annual Family Income: ${userData.income}\n`;
            if (userData.field) content += `Field of Interest: ${userData.field}\n`;
            if (userData.email) content += `Email: ${userData.email}\n`;
            content += `\nREPORT GENERATED ON: ${new Date().toLocaleDateString()}\n\n`;

            // Add summary statistics
            content += `SUMMARY STATISTICS\n`;
            content += `${'─'.repeat(25)}\n`;
            content += `• Total Scholarships Found: ${results.scholarships.length}\n`;
            content += `• Government Schemes Available: ${results.schemes.length}\n`;
            content += `• Competitive Exams Eligible: ${results.exams.length}\n`;
            content += `• Job Opportunities: ${results.jobs.length}\n\n`;

            const categories = ['scholarships', 'schemes', 'exams', 'jobs'];
            const categoryTitles = ['SCHOLARSHIPS', 'GOVERNMENT SCHEMES', 'COMPETITIVE EXAMS', 'GOVERNMENT JOBS'];

            categories.forEach((category, index) => {
                if (results[category].length > 0) {
                    content += `${categoryTitles[index]}\n`;
                    content += `${'─'.repeat(categoryTitles[index].length)}\n`;
                    results[category].forEach((item, itemIndex) => {
                        content += `${itemIndex + 1}. ${item.name}\n`;
                        content += `   Description: ${item.description}\n`;
                        
                        // Add category-specific details
                        switch(category) {
                            case 'scholarships':
                                if (item.amount) content += `   Amount: ${item.amount}\n`;
                                if (item.deadline) content += `   Deadline: ${item.deadline}\n`;
                                if (item.type) content += `   Type: ${item.type}\n`;
                                break;
                            case 'schemes':
                                if (item.benefit) content += `   Benefit: ${item.benefit}\n`;
                                if (item.type) content += `   Type: ${item.type}\n`;
                                break;
                            case 'exams':
                                if (item.examDate) content += `   Exam Date: ${item.examDate}\n`;
                                if (item.applicationFee) content += `   Application Fee: ${item.applicationFee}\n`;
                                break;
                            case 'jobs':
                                if (item.salary) content += `   Salary: ${item.salary}\n`;
                                if (item.vacancies) content += `   Vacancies: ${item.vacancies}\n`;
                                if (item.applicationDeadline) content += `   Application Deadline: ${item.applicationDeadline}\n`;
                                break;
                        }
                        content += `\n`;
                    });
                    content += `\n`;
                }
            });

            // Add footer
            content += `${'='.repeat(50)}\n`;
            content += `This report was generated by Eligibuddy\n`;
            content += `For more information, visit our website or contact support.\n`;
            content += `\nDISCLAIMER: This report is for informational purposes only.\n`;
            content += `Please verify all details from official sources before applying.\n`;

            // Create and download file
            const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `eligibility_report_${userData.firstName}_${userData.lastName}_${new Date().toISOString().split('T')[0]}.txt`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        }

        // Print functionality
        function printResults() {
            if (!currentResults) {
                alert('Please check eligibility first!');
                return;
            }
            window.print();
        }

        // Share functionality
        function shareResults() {
            if (!currentResults) {
                alert('Please check eligibility first!');
                return;
            }

            const { results, userData } = currentResults;
            const totalOpportunities = results.scholarships.length + results.schemes.length + 
                                     results.exams.length + results.jobs.length;

            const shareText = `🎯 Found ${totalOpportunities} opportunities on Eligibuddy!\n\n` +
                            `📊 My Results:\n` +
                            `🎓 ${results.scholarships.length} Scholarships\n` +
                            `🏛️ ${results.schemes.length} Government Schemes\n` +
                            `📝 ${results.exams.length} Competitive Exams\n` +
                            `💼 ${results.jobs.length} Job Opportunities\n\n` +
                            `Check your eligibility at Eligibuddy! #EligibilityCheck #Opportunities`;

            if (navigator.share) {
                navigator.share({
                    title: 'My Eligibility Results - Eligibuddy',
                    text: shareText,
                    url: window.location.href
                });
            } else {
                // Fallback for browsers that don't support native sharing
                navigator.clipboard.writeText(shareText).then(() => {
                    alert('Results copied to clipboard! You can now share them.');
                }).catch(() => {
                    alert('Sharing not supported on this browser. You can manually copy the URL to share.');
                });
            }
        }

        // Navbar scroll effect
        window.addEventListener('scroll', function() {
            const navbar = document.getElementById('navbar');
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });

        // Form validation enhancements
        document.querySelectorAll('input, select').forEach(element => {
            element.addEventListener('blur', function() {
                validateField(this);
            });
        });

        function validateField(field) {
            try {
                const value = field.value.trim();
                const isRequired = field.hasAttribute('required');
                
                // Remove existing validation styles
                field.classList.remove('error', 'success');
                
                if (isRequired && !value) {
                    field.classList.add('error');
                    return false;
                } else if (field.type === 'email' && value) {
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(value)) {
                        field.classList.add('error');
                        return false;
                    }
                } else if (field.type === 'number' && value) {
                    const num = parseInt(value);
                    const min = parseInt(field.min);
                    const max = parseInt(field.max);
                    if (isNaN(num) || (min && num < min) || (max && num > max)) {
                        field.classList.add('error');
                        return false;
                    }
                }
                
                if (value) {
                    field.classList.add('success');
                }
                return true;
            } catch (error) {
                console.error('Validation error:', error);
                return false;
            }
        }

        // Auto-save form data to localStorage (for better UX)
        function saveFormData() {
            const formData = new FormData(document.getElementById('eligibilityForm'));
            const data = {};
            for (let [key, value] of formData.entries()) {
                data[key] = value;
            }
            // Note: localStorage is not available in Claude artifacts, but this would work in a real browser
            // localStorage.setItem('eligibilityFormData', JSON.stringify(data));
        }

        // Initialize the application
        function initializeEligibilityPage() {
            updateAuthenticationUI();
            preloadEligibilityDatabase();

            document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                anchor.addEventListener('click', function (e) {
                    e.preventDefault();
                    const target = document.querySelector(this.getAttribute('href'));
                    if (target) {
                        target.scrollIntoView({ behavior: 'smooth' });
                    }
                });
            });

            const form = document.getElementById('eligibilityForm');
            if (form) {
                form.addEventListener('input', saveFormData);
            }

            document.addEventListener('keydown', function(e) {
                if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
                    document.getElementById('eligibilityForm').dispatchEvent(new Event('submit'));
                }

                if (e.key === 'Escape') {
                    closeModal();
                }
            });
        }

        // Function to update UI based on authentication status
        function updateAuthenticationUI() {
            const isAuthenticated = isUserAuthenticated();
            const loginRequired = document.getElementById('loginRequired');
            const buttonText = document.getElementById('buttonText');
            
            if (!isAuthenticated) {
                // Show login required message
                if (loginRequired) loginRequired.style.display = 'block';
                if (buttonText) buttonText.textContent = 'Login to Analyze';
            } else {
                // Hide login required message
                if (loginRequired) loginRequired.style.display = 'none';
                if (buttonText) buttonText.textContent = 'Analyze My Eligibility';
            }
        }

function buildAssistantContext() {
    const context = {
        page: 'home',
        authenticated: isUserAuthenticated(),
        profile: {},
        results: {}
    };

    const form = document.getElementById('eligibilityForm');
    if (form) {
        const formData = new FormData(form);
        const profileFields = [
            'firstName',
            'lastName',
            'age',
            'gender',
            'state',
            'district',
            'qualification',
            'category',
            'income',
            'email',
            'field',
            'disability'
        ];

        profileFields.forEach((fieldName) => {
            const value = (formData.get(fieldName) || '').toString().trim();
            if (value) {
                context.profile[fieldName] = value;
            }
        });

        const preferences = formData.getAll('preferences').map((item) => item.toString());
        if (preferences.length > 0) {
            context.profile.preferences = preferences;
        }
    }

    if (currentResults && currentResults.results) {
        context.results = {
            scholarships: currentResults.results.scholarships.length,
            schemes: currentResults.results.schemes.length,
            exams: currentResults.results.exams.length,
            jobs: currentResults.results.jobs.length,
            currentFilter
        };
    }

    return context;
}

class EligibuddyAssistantWidget {
    constructor() {
        this.messagesContainer = document.getElementById('geminiMessages');
        this.inputField = document.getElementById('geminiInput');
        this.sendButton = document.getElementById('geminiSend');
        this.loadingIndicator = document.getElementById('geminiLoading');
        this.widget = document.getElementById('geminiWidget');
        this.fab = document.getElementById('geminiToggle');
        this.minimizeButton = document.getElementById('minimizeChat');

        this.initializeEventListeners();
    }

    initializeEventListeners() {
        this.fab.addEventListener('click', () => this.toggleWidget());
        this.minimizeButton.addEventListener('click', () => this.hideWidget());
        this.sendButton.addEventListener('click', () => this.sendMessage());

        this.inputField.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });

        this.inputField.addEventListener('input', () => {
            this.sendButton.disabled = !this.inputField.value.trim();
        });
    }

    toggleWidget() {
        if (this.widget.classList.contains('active')) {
            this.hideWidget();
        } else {
            this.showWidget();
        }
    }

    showWidget() {
        this.widget.classList.add('active');
        this.fab.classList.add('hidden');
        this.inputField.focus();
    }

    hideWidget() {
        this.widget.classList.remove('active');
        this.fab.classList.remove('hidden');
    }

    async sendMessage() {
        const message = this.inputField.value.trim();
        if (!message) {
            return;
        }

        this.addMessage(message, 'user');
        this.inputField.value = '';
        this.sendButton.disabled = true;
        this.showLoading();

        try {
            const response = await fetch('/api/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    message,
                    context: buildAssistantContext()
                })
            });

            const data = await response.json();
            this.hideLoading();

            if (response.status === 401) {
                window.location.href = '/login';
                return;
            }

            if (response.ok) {
                this.addMessage(data.response, 'ai');
            } else {
                this.showError(data.error || 'I could not answer that right now. Please try again.');
            }
        } catch (error) {
            this.hideLoading();
            console.error('Assistant request failed:', error);
            this.showError('I could not reach the assistant right now. Please try again in a moment.');
        }
    }

    addMessage(text, type) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `gemini-message ${type}`;

        const formattedText = text
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .replace(/`(.*?)`/g, '<code style="background: #f1f3f4; padding: 2px 4px; border-radius: 3px;">$1</code>')
            .replace(/\n/g, '<br>');

        messageDiv.innerHTML = formattedText;
        this.messagesContainer.appendChild(messageDiv);
        this.scrollToBottom();
    }

    showError(errorMessage) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'gemini-error';
        errorDiv.textContent = errorMessage;
        this.messagesContainer.appendChild(errorDiv);
        this.scrollToBottom();
    }

    showLoading() {
        this.loadingIndicator.classList.add('active');
        this.scrollToBottom();
    }

    hideLoading() {
        this.loadingIndicator.classList.remove('active');
    }

    scrollToBottom() {
        this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
    }
}

function askQuickQuestion(question) {
    const input = document.getElementById('geminiInput');
    if (!input) {
        return;
    }

    input.value = question;
    if (window.eligibuddyAssistant) {
        window.eligibuddyAssistant.sendMessage();
    }
}

function toggleContext() {
    const details = document.getElementById('contextDetails');
    const icon = document.getElementById('contextIcon');
    if (!details || !icon) {
        return;
    }

    if (details.classList.contains('show')) {
        details.classList.remove('show');
        icon.style.transform = 'rotate(0deg)';
    } else {
        details.classList.add('show');
        icon.style.transform = 'rotate(180deg)';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    initializeEligibilityPage();

    if (!document.getElementById('geminiWidget')) {
        return;
    }

    window.eligibuddyAssistant = new EligibuddyAssistantWidget();
    window.geminiAssistant = window.eligibuddyAssistant;
});
