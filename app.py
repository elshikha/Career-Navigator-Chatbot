# PACKAGES
import os
import time
import csv
import requests
import pandas as pd
import streamlit as st
from bs4 import BeautifulSoup

# Function to scrape job postings from LinkedIn for a specific job role
def scrape_linkedin_jobs(job_role, loc):
    # Construct the search URL for the specific job role in the specified location
    url = f'https://www.linkedin.com/jobs/search?location={loc}&keywords={job_role}'
    
    # Send a GET request to the URL
    response = requests.get(url)
    
    # Check if request was successful
    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # Extract job postings, limiting to 10
        job_postings = soup.find_all("div",class_="base-card relative w-full hover:no-underline focus:no-underline base-card--link base-search-card base-search-card--link job-search-card")[:10]
        
        # Store job postings in a list of dictionaries
        job_data = []
        for posting in job_postings:
            title = posting.find('h3', class_='base-search-card__title').text.strip()
            company = posting.find('h4', class_='base-search-card__subtitle').text.strip()
            location = posting.find('span', class_='job-search-card__location').text.strip()
            link = posting.find("a", class_="base-card__full-link")["href"]

            job_data.append({'Title': title, 'Company': company, 'Location': location, 'Link': link})
        return job_data
    
# Function to refresh the dataset for a new role
def refresh_dataset(role,location):
    # Loop through each job role and scrape LinkedIn for job postings
    job_postings = scrape_linkedin_jobs(role, location)
    job_postings = job_postings[:10]

    # Write all job postings to a single CSV file
    with open('datasets/all_linkedin_jobs.csv', 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['Title', 'Company', 'Location', 'Link']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        for job_posting in job_postings:
            writer.writerow(job_posting)

# Function to list all files in the "conversations" folder
def list_files_in_folder(folder_path):
    files = []
    for file in os.listdir(folder_path):
        if os.path.isfile(os.path.join(folder_path, file)):
            files.append(file)
    return files

# print scala output
def printscala(user_input):
    with open('connection/pyinput.txt', 'w') as file:
        file.write(user_input + '\n')
    time.sleep(2)
    with open('connection/scalaoutput.txt', 'r') as file:
        scala_output = file.read()
        open('connection/scalaoutput.txt', 'w').close()
        if scala_output != "":
            return scala_output

# MAIN + STREAMLIT PAGE LAYOUT
def main():
    # Page Confing
    st.set_page_config(page_title="Career Navigator", page_icon="::🖥️:")
    
    # Page title
    st.title("Career Navigator 🧑🏻‍💻\nHow can I help you today?")

    # Initialize conversation history if not present
    if "conversation" not in st.session_state:
        st.session_state.conversation = None

    # Initialize memory for storing past questions and answers
    if "chat_memory" not in st.session_state:
        st.session_state.chat_memory = []

    # ROLES FOR CHAT MEMORY
    USER = "user"
    ASSISTANT = "assistant"   
    
    # USER INPUT
    user_question = st.chat_input("Ask Career Navigator a question...")
    
    # Sidebar functionality

    # Chat history
    st.sidebar.header("Chat history")
    newchatbttn = st.sidebar.button("New chat :pencil:")
    conversation_files = list_files_in_folder("conversations")
    conversation_files = conversation_files[:len(conversation_files)-1]
    if len(conversation_files) == 0:
        open('conversations/chathistory_0.txt', 'w')
        conversation_files = list_files_in_folder("conversations")
        conversation_files = conversation_files[:len(conversation_files)-1]   
    selected_file = st.sidebar.selectbox("Recent chats", conversation_files[::-1])

    # Conversation folder path
    conversations_dir = 'conversations/'

    # Display the selected conversation file
    if selected_file != open(conversations_dir + 'currentchat.txt', 'r').readline():
        st.session_state.chat_memory = []
        with open(conversations_dir + 'currentchat.txt', 'w') as file:
                file.write(selected_file)
        with open(conversations_dir + selected_file, 'r+') as file:
            lines = file.readlines()
            cleaned_lines = [line.strip() for line in lines]
            open(conversations_dir+selected_file, 'w').close()
            for line in cleaned_lines:
                response = printscala(line)
                st.session_state.chat_memory.append({'question': line, 'answer': response})
                 
    if newchatbttn:
        last_file_number = int(conversation_files[-1].split("_")[1].split(".")[0])
        new_filename = f'chathistory_{last_file_number + 1}.txt'
        with open(conversations_dir + 'currentchat.txt', 'w') as file:
            file.write(new_filename)
        open(conversations_dir+new_filename,'w')
        st.session_state.chat_memory = []
        st.rerun()

    # Find a job func
    st.sidebar.header("Find a job!")
    df = pd.read_csv("datasets/jobs_DSQR.csv")
    job_roles = df["Job Title"]
    job_roles_options = ["Select a role"] + job_roles.tolist()
    selected_role = st.sidebar.selectbox("Role", job_roles_options)
    locations = ["Select a location", "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia", "Australia",
                "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados","Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina",
                "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde","Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile", "China",
                "Colombia", "Comoros", "Congo", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic","Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor",
                "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji","Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala",
                "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary", "Iceland", "India", "Indonesia","Iran", "Iraq", "Ireland", "Italy", "Ivory Coast", "Jamaica", "Japan", "Jordan", "Kazakhstan",
                "Kenya", "Kiribati", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho","Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia",
                "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia","Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru",
                "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Macedonia","Norway", "Oman", "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru",
                "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis","Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe",
                "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia","Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan",
                "Suriname", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga","Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates",
                "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela","Vietnam", "Yemen", "Zambia", "Zimbabwe"]
    selected_loc = st.sidebar.selectbox("Location", locations)
    findjob_button = st.sidebar.button("Find the job")
    if findjob_button and selected_role != "Select a role" and selected_loc != "Select a location":
        refresh_dataset(selected_role, selected_loc)
        linkedinjobsdf = pd.read_csv("datasets/all_linkedin_jobs.csv")
        linkedinjobs = []
        for row in range(len(linkedinjobsdf)):
            title = linkedinjobsdf['Title'][row]
            company = linkedinjobsdf['Company'][row]
            location = linkedinjobsdf['Location'][row]
            link = linkedinjobsdf['Link'][row]
            linkedinjobs.append(f"- [{title} at {company} in {location}]({link})")
        response = f"Here are some jobs you can apply for as a {selected_role} in {selected_loc}:\n"
        response += "\n".join(linkedinjobs)
        st.session_state.chat_memory.append({'question': f"Find me a job as a {selected_role} in {selected_loc}",'answer': response})
        st.sidebar.success("Done")

    
    # Display past questions and answers
    for interaction in st.session_state.chat_memory:
        st.chat_message(USER).write(interaction['question'])
        st.chat_message(ASSISTANT).write(interaction['answer'])
        
    if user_question:
        with st.spinner("Generating response..."):
            open('connection/pyinput.txt', 'w').close()
            open('connection/scalaoutput.txt', 'w').close()
            response = printscala(user_question)
            st.chat_message(USER).write(user_question)
            st.chat_message(ASSISTANT).write(response)
            st.session_state.chat_memory.append({'question': user_question, 'answer': response})


if __name__ == '__main__':
    main()