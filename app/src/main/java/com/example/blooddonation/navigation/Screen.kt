package com.example.blooddonation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignUp : Screen("signup")
    object SignIn : Screen("signin")
    object AdminDashboard : Screen("admin_dashboard")

    object Profile : Screen("profile") {
        const val ARG_UID = "uid"
        const val routeWithArgs = "profile/{$ARG_UID}"
        fun createRoute(uid: String) = "profile/$uid"
    }

    object Dashboard : Screen("dashboard") {
        const val ARG_UID = "uid"
        const val ARG_USERNAME = "username"
        const val ARG_IMAGE_URI = "imageUriEncoded"
        const val routeWithUid = "dashboard/{$ARG_UID}"
        const val routeWithProfile = "dashboard/{$ARG_USERNAME}/{$ARG_IMAGE_URI}/{$ARG_UID}"
        fun createRoute(uid: String) = "dashboard/$uid"
        fun createRoute(username: String, imageUriEncoded: String, uid: String) =
            "dashboard/$username/$imageUriEncoded/$uid"
    }

    object AboutUs : Screen("about_us")
    object OurWork : Screen("our_work")
    object Help : Screen("help")

    object ViewDonors : Screen("view_donors") {
        const val ARG_USER = "currentUserId"
        const val routeWithArgs = "view_donors/{$ARG_USER}"
        fun createRoute(uid: String) = "view_donors/$uid"
    }

    object RequestBlood : Screen("request_blood") {
        const val ARG_USER = "currentUserId"
        const val routeWithArgs = "request_blood/{$ARG_USER}"
        fun createRoute(uid: String) = "request_blood/$uid"
    }

    object MyProfile : Screen("my_profile") {
        const val ARG_UID = "uid"
        const val routeWithArgs = "my_profile/{$ARG_UID}"
        fun createRoute(uid: String) = "my_profile/$uid"
    }

    object BloodCampList : Screen("blood_camp_list")

    object Chat : Screen("chat") {
        const val ARG_CHAT_ID = "chatId"
        const val ARG_CURRENT_ID = "currentUserId"
        const val ARG_OTHER_ID = "otherUserId"
        const val routeWithArgs = "chat/{$ARG_CHAT_ID}/{$ARG_CURRENT_ID}/{$ARG_OTHER_ID}"
        fun createRoute(chatId: String, currentId: String, otherId: String) =
            "chat/$chatId/$currentId/$otherId"
    }
}
