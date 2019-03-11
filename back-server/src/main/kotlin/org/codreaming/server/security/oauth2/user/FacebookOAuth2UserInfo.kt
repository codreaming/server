package org.codreaming.server.security.oauth2.user

class FacebookOAuth2UserInfo(
        attributes: MutableMap<String, Any>
) : OAuth2UserInfo(attributes) {
    override fun getId() = attributes["id"] as String

    override fun getName() = attributes["name"] as String

    override fun getEmail() = attributes["email"] as String

    override fun getImageUrl(): String {
        if (attributes.containsKey("picture")) {
            val pictureObj = attributes["picture"] as Map<*, *>
            if (pictureObj.containsKey("data")) {
                val dataObj = pictureObj["data"] as Map<*, *>
                if (dataObj.containsKey("url")) {
                    return dataObj["url"] as String
                }
            }
        }
        return ""
    }
}
