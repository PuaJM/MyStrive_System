/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


// Helper function to display an error message
function showError(elementId, message) {
    const errorElement = document.getElementById(elementId + 'Error');
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.color = '#f44336'; // Red color for errors
        errorElement.style.fontSize = '0.85em';
        errorElement.style.marginTop = '5px';
    }
}

// Helper function to clear an error message
function clearError(elementId) {
    const errorElement = document.getElementById(elementId + 'Error');
    if (errorElement) {
        errorElement.textContent = '';
    }
}


function validateRegisterForm() {
    let isValid = true;

    // Get input elements
    const username = document.getElementById('username');
    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    // Clear previous errors
    clearError('username');
    clearError('email');
    clearError('password');
    clearError('confirmPassword');

    // Validate Username
    if (!username || username.value.trim() === '') {
        showError('username', 'Username is required.');
        isValid = false;
    }

    // Validate Email
    if (!email || email.value.trim() === '') {
        showError('email', 'Email is required.');
        isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())) {
        showError('email', 'Please enter a valid email address.');
        isValid = false;
    }

    // Validate Password
    if (!password || password.value.trim() === '') {
        showError('password', 'Password is required.');
        isValid = false;
    } else if (password.value.length < 6) {
        showError('password', 'Password must be at least 6 characters long.');
        isValid = false;
    }

    // Validate Confirm Password
    if (!confirmPassword || confirmPassword.value.trim() === '') {
        showError('confirmPassword', 'Confirm Password is required.');
        isValid = false;
    } else if (password && confirmPassword && password.value !== confirmPassword.value) {
        showError('confirmPassword', 'Passwords do not match.');
        isValid = false;
    }

    return isValid;
}


function validateLoginForm() {
    let isValid = true;

    const username = document.getElementById('username');
    const password = document.getElementById('password');

    clearError('username');
    clearError('password');

    if (!username || username.value.trim() === '') {
        showError('username', 'Username is required.');
        isValid = false;
    }

    if (!password || password.value.trim() === '') {
        showError('password', 'Password is required.');
        isValid = false;
    }

    return isValid;
}


function validateGoalForm() {
    let isValid = true;

    const goalDescription = document.getElementById('goalDescription');
    const categoryId = document.getElementById('categoryId');
    const targetDate = document.getElementById('targetDate');
    const status = document.getElementById('status');

    clearError('goalDescription');
    clearError('categoryId');
    clearError('targetDate');
    clearError('status');

    // Validate Goal Description
    if (!goalDescription || goalDescription.value.trim() === '') {
        showError('goalDescription', 'Goal description is required.');
        isValid = false;
    }


    // Validate Target Date
    if (!targetDate || targetDate.value.trim() === '') {
        showError('targetDate', 'Target Date is required.');
        isValid = false;
    } else {
        const selectedDate = new Date(targetDate.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Reset time for accurate date comparison

        if (selectedDate < today) {
            showError('targetDate', 'Target Date cannot be in the past.');
            isValid = false;
        }
    }

    // Validate Status
    if (!status || status.value.trim() === '') {
        showError('status', 'Status is required.');
        isValid = false;
    }

    return isValid;
}


function validateCategoryForm() {
    let isValid = true;

    const categoryName = document.getElementById('categoryName');

    clearError('categoryName');

    // Validate Category Name
    if (!categoryName || categoryName.value.trim() === '') {
        showError('categoryName', 'Category Name is required.');
        isValid = false;
    }

    return isValid;
}

function validateMilestoneForm() {
    let isValid = true;

    const milestoneDescription = document.getElementById('milestoneDescription');
    const dueDate = document.getElementById('dueDate');
    const status = document.getElementById('milestoneStatus'); // Note: ID is milestoneStatus

    clearError('milestoneDescription');
    clearError('dueDate');
    clearError('milestoneStatus');

    // Validate Milestone Description
    if (!milestoneDescription || milestoneDescription.value.trim() === '') {
        showError('milestoneDescription', 'Milestone description is required.');
        isValid = false;
    }

    // Validate Due Date
    if (!dueDate || dueDate.value.trim() === '') {
        showError('dueDate', 'Due Date is required.');
        isValid = false;
    } else {
        const selectedDate = new Date(dueDate.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Reset time for accurate date comparison

        if (selectedDate < today) {
            showError('dueDate', 'Due Date cannot be in the past.');
            isValid = false;
        }
    }

    // Validate Status
    if (!status || status.value.trim() === '') {
        showError('milestoneStatus', 'Status is required.');
        isValid = false;
    }

    return isValid;
}

