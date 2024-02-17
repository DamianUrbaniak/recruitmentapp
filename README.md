
Spring Boot GitHub API Client
Overview
This Spring Boot application serves as a GitHub API client, allowing users to retrieve a list of repositories along with their branches and the latest commit SHA for a given GitHub username. 
The application exposes a single endpoint that accepts the username as a path variable and requires the application/json header. 
If the provided GitHub username does not exist, the application will return a 404 status with an error message indicating that the user was not found.

#### Example Response for Non-Existent User

{
  "status": 404,
  "message": "User not found."
}

Getting Started
Clone the Repository:
git clone https://github.com/DamianUrbaniak/recruitmentapp.git

Build the Project:
cd recruitmentapp
./gradlew build

Run the project
./gradlew bootRun

The application will start on http://localhost:8080.

API Endpoint
Get Repositories with Branches and Latest SHA
Endpoint: /api/github/fetch-repo-data/{userName}
Method: GET
Headers:
Content-Type: application/json

Example response:

[
    {
        "repoName": "Repostory name",
        "owner": "Repository owner",
        "branches": [
            {
                "name": "master",
                "lastSha": "b33a9c7c02ad93f621cp38f0e9fc9e867e12fa0e"
            }
        ]
    }
]


License
This project is licensed under the MIT License - see the LICENSE file for details.
