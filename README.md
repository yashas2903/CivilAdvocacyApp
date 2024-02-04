## Overview

Civil Advocacy, an Android application, is designed to assist users in discovering and engaging with political officials representing their current location or a specified area. Leveraging Location Services, Internet, Google APIs, Images, Picasso Library, Implicit Intents, and TextView Links, the app aims to deliver a seamless user experience.

## App Highlights

- Obtain and showcase an interactive list of political officials.
- Utilize Android Fused Location Provider services to determine the user's current location.
- Retrieve government official data from the Google Civic Information API through a REST service and JSON results.
- Implement different layouts for landscape orientation in specific activities.
- Enable users to access detailed information about a representative by clicking on an official's list entry.
- Display a larger version of an official's photo in the Photo Activity upon clicking the respective image.
- About activity providing application information, including Author, Copyright data, and Version.

## Required Permissions

- ACCESS_FINE_LOCATION
- ACCESS_COARSE_LOCATION
- INTERNET

## API Key

To utilize the Google Civic Information API, acquiring an API key is essential. Follow these steps to obtain your key:

1. Visit the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. Navigate to the "APIs & Services" > "Credentials" section.
4. Click on "Create Credentials" and select "API Key".
5. Copy your API key.

## Application Behavior Diagrams

### 1. Main Activity

- Display a RecyclerView list of government officials.
- Provide options menu items for "about" information and manual location entry.
- Clicking on an official's entry opens a new activity containing detailed information on the selected individual.

### 2. Official Activity

- Present contact information for the selected official.
- Utilize background color based on the official's political party.
- Display party logos for Democratic & Republican officials.

### 3. Photo Detail Activity

- Showcase a full-sized image of the official.
- Implement background color based on the official's political party.
- Display party logos for Democratic & Republican officials.

### 4. About Activity

- Display application information, including author, copyright data, and version.

## Development Plan

1. Create the base app with MainActivity, Official Class, RecyclerView Adapter, ViewHolder, and About Activity.
2. Implement location code and dummy data for testing.
3. Integrate Official Activity with social media and contact information.
4. Incorporate the Google Civic Information API for real data retrieval.
5. Conduct thorough testing against all outlined requirements.

## Usage

1. Clone the repository.
2. Add your Google Civic Information API key to the project.
3. Open the project in Android Studio.
4. Build and run the app on an Android emulator or device.
