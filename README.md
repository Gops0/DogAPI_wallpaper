# Dog Images App 🐶

A simple Android application to browse random dog images, save favorites, and set them as your wallpaper. The app uses the [Dog CEO API](https://dog.ceo/dog-api/) for fetching dog images.

## Features ✨
- **Browse Random Dog Images:** Get a fresh batch of random dog images on every refresh.
- **Pull-to-Refresh:** Swipe down to load new images.
- **Favorites:** Save your favorite dog images to view them later.
- **Set as Wallpaper:** Quickly set any dog image as your device wallpaper.
- **Responsive UI:** Intuitive and user-friendly interface with RecyclerView for seamless scrolling.

## Screenshots 📸
| Home Screen  | Favorites Screen | Wallpaper Set Success |
|--------------|------------------|-----------------------|
| ![Home](screenshots/home_screen.png) | ![Favorites](screenshots/favorites_screen.png) | ![Wallpaper Success](screenshots/wallpaper_success.png) |

## Demo 🎥
[Watch the demo video here](https://github.com/your-username/dog-images-app/demo.mp4)

## Installation 📥

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/dog-images-app.git
2.Open the project in Android Studio.
3.Sync the Gradle files.
4.Run the app on an Android device or emulator.


## Tech Stack 🛠️
- **Language:** Java
- **API:** Dog CEO API
- **Networking Library:** [OkHttp](https://square.github.io/okhttp/)
- **Image Loading Library:** [Glide](https://github.com/bumptech/glide)
- **UI Components:** RecyclerView, SwipeRefreshLayout

## Usage 🚀
1. **Launch the app** to view a list of random dog images.
2. **Pull down to refresh** the list with new images.
3. **Tap on any image** to view breed details or long-press to get additional options.
4. **Save your favorite images** for quick access later.
5. **Set any image as your device wallpaper** with a single tap.

## Project Structure 🗂️
```plaintext
├── adapter/
│   └── DogImageAdapter.java   # RecyclerView adapter for dog images
├── model/
│   └── DogImage.java          # Model class for dog images
├── ui/
│   ├── MainActivity.java      # Main screen of the app
│   ├── FavoritesActivity.java # Screen to display favorite images
│   └── BreedDetailActivity.java # Screen to display breed details
├── res/
│   ├── layout/                # XML layout files
│   ├── drawable/              # App icons and images
│   └── values/                # Strings, colors, and styles
└── build.gradle               # Gradle configuration

# Dog Images App API & Credits 📜

## API Reference 🔗
The app uses the **Dog CEO API** to fetch random dog images.

- **Endpoint:** `https://dog.ceo/api/breeds/image/random/10`
- **Response Example:**
  ```json
  {
    "message": [
      "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
      "https://images.dog.ceo/breeds/hound-afghan/n02088094_1023.jpg",
      ...
    ],
    "status": "success"
  }



Here’s the standalone README.md file for the API Reference, Contributing, License, and Acknowledgements sections:

markdown
Copy code
# Dog Images App API & Credits 📜

## API Reference 🔗
The app uses the **Dog CEO API** to fetch random dog images.

- **Endpoint:** `https://dog.ceo/api/breeds/image/random/10`
- **Response Example:**
  ```json
  {
    "message": [
      "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
      "https://images.dog.ceo/breeds/hound-afghan/n02088094_1023.jpg",
      ...
    ],
    "status": "success"
  }
Contributing 🤝
Contributions are welcome! If you'd like to improve this app:

Fork the repository.
Create a new branch: git checkout -b feature/YourFeature.
Commit your changes: git commit -m "Added YourFeature".
Push to the branch: git push origin feature/YourFeature.
Submit a pull request.
License 📄
This project is licensed under the MIT License. See the LICENSE file for details.

Acknowledgements 🙏
Dog CEO API: For providing the free dog image API.
Glide: For effortless image loading.
OkHttp: For robust HTTP requests.
Made with ❤️ by Your Name

markdown
Copy code



