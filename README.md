# BlogSpace
A platform that allows users to create, view, update, and delete blogs. Users can define attributes such as title, author, and content, and manage posts efficiently with pagination.

# Application Endpoints
  Create a Blog : user can create blog by providing details are Title,Author and Content
    - if use create successful then in response it can see blog details include created blog time and status code will be "201"
    - if any issue occur while in data then status code will be "500"
  Retrive list of Blog : user can retrive all blogs but in a single page there is limited blog can provided 
    - For single page maximum 7 blogs can retrive if more than 7 then it will comes under in next page 
    - if any issue occurs while retriving the data then 500 status code other wise 200 
  Retrive single Blog : The user see blog details 
    - if use retrive blog then status code 200 other wise 500
  Update a blog : user can modify the blog details 
    - if it successful to modify then 200 otherwise status will be 500 
  Delete a blog : user can remove the blog 
    - if to remove blog the use successfull then 200 status otherwise status will be 500 
