console.log("this is script file");


const toggleSidebar = () => {
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");

    if (window.getComputedStyle(sidebar).display === "none") {
        sidebar.style.display = "block";
        content.style.marginLeft = "20%";
    } else {
        sidebar.style.display = "none";
        content.style.marginLeft = "0";
    }
};	
	

