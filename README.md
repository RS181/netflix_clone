<h2>Developers</h2>
<ol>
  <li> Rui Santos </li>
  <li> Kenny Correia </li>
</ol>
<br>
<h2>Content within the project</h2>
<ul>
  <li> User application used to watch streams. </li>
  <li>CMS application used to add/delete movies and users. </li>
  <li> Application Server is responsible for storing , acessing and processing of information related to users and
    streams. </li>
</ul>
<br>
<h2>Project Description</h2>
<ol>
  <li> Android Application's</li>
  <ul>
    <li>For both User and CMS application , we used Android Studio. </li>
    <li>Both applications are equiped with login and register features. </li>
    <li>The GUI of each application , was designed to give good user/admin experience. </li>
  </ul>
  <li>Application Server</li>
  <ul>
    <li> MYSQL </li>
    <ul>
      <li>For permanent storing , we used a relational database.</li>
    </ul>
    <li> Nginx </li>
    <ul>
      <li> We used Nginx for dealing with diferent tasks , the most importants to note are:</li>
      <ul>
        <li> Watching streams via the internet.</li>
        <li>Mediating an ecrypted communication between application's and Application Server (using https).</li>
      </ul>
    </ul>
    <li> Jetty+Jersey </li>
    <ul>
      <li>It's used as a foundation of the Application Server, and it was developed using the maven building tool.</li>
      <li>For each uploaded movie , it generates two versions (360p and 1080p) with the help of ffmpeg.</li>
      <li>It's also responsible for storing information in the Database.</li>
    </ul>
  </ul>
</ol>
<br>
<h2>Project Images</h2>
The following images are screenshots of our project. <br>

<h3>CMS APP</h3>
<img src="https://github.com/RS181/netflix_clone/assets/127786118/064f0bf1-1991-4bec-97e8-a743b1228d34" with=200
  height=400 alt="image1">
<img src="https://github.com/RS181/netflix_clone/assets/127786118/f4134c74-c845-438a-8eeb-864c1626aed6" with=200
  height=400 alt="image2">
  <img src="https://github.com/RS181/netflix_clone/assets/127786118/2c60458f-1a3a-4673-bd07-45fad87a44ad" with=200
  height=400 alt="image3">
    <img src="https://github.com/RS181/netflix_clone/assets/127786118/bebd4769-edf0-4b43-9ede-e27ef64ab38b" with=200
  height=400 alt="image4">
      <img src="https://github.com/RS181/netflix_clone/assets/127786118/3049b9e2-db0b-424d-a83f-6b5282f001f4" with=200
  height=400>
        <img src="https://github.com/RS181/netflix_clone/assets/127786118/ba0ca723-5579-4326-bd4e-c14ed3de72f5" with=200
  height=400 alt="image5">
  


<br>
<h3>Client APP</h3>
        <img src="https://github.com/RS181/netflix_clone/assets/127786118/1d8c79f0-3d22-43d9-b422-5ccc102c5dd0" with=200
  height=400 alt="image6">
          <img src="https://github.com/RS181/netflix_clone/assets/127786118/52c77293-8bb3-4670-860b-7b59c7511905" with=200
  height=400 alt="image7">
          <img src="https://github.com/RS181/netflix_clone/assets/127786118/10ce821a-a7a8-4206-ba14-f8caa8eccd58" with=200
  height=400 alt="image8">

![060fe42b-782a-4a22-89c1-ac25080e1a7e](https://github.com/RS181/netflix_clone/assets/127786118/10ce821a-a7a8-4206-ba14-f8caa8eccd58)




> [!NOTE]
> The movies weren't saved in the database it self but rather it's path to the folder where they were stored.
