# Composables

## Q1: What are the basic composables in Compose?

```kotlin
// Text — display text
Text("Hello, World!", style = MaterialTheme.typography.headlineMedium)

// Button — clickable button
Button(onClick = { /* action */ }) {
    Text("Click Me")
}

// Image — display image
Image(painter = painterResource(R.drawable.logo), contentDescription = "Logo")

// Icon — vector icon
Icon(Icons.Default.Home, contentDescription = "Home")

// Box — stack children (like FrameLayout)
Box {
    Text("Background")
    Text("Foreground")
}

// Column — vertical list (like LinearLayout vertical)
Column {
    Text("First")
    Text("Second")
}

// Row — horizontal list (like LinearLayout horizontal)
Row {
    Text("Left")
    Text("Right")
}
```

---

## Q2: How do you use Text composable?

```kotlin
// Basic
Text("Hello")

// With style
Text(
    text = "Hello, $name!",
    color = Color.Blue,
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
    fontStyle = FontStyle.Italic,
    fontFamily = FontFamily.Serif,
    letterSpacing = 1.sp,
    textDecoration = TextDecoration.Underline,
    textAlign = TextAlign.Center,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
)

// Annotated string — mixed styles
Text(buildAnnotatedString {
    append("Hello, ")
    withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
        append("World!")
    }
})

// Clickable text
Text(
    text = "Click here",
    modifier = Modifier.clickable { /* handle click */ },
    color = Color.Blue,
    textDecoration = TextDecoration.Underline,
)
```

---

## Q3: How do you use Button composables?

```kotlin
// Button
Button(onClick = { /* action */ }) {
    Text("Submit")
}

// OutlinedButton
OutlinedButton(onClick = { /* action */ }) {
    Text("Cancel")
}

// TextButton
TextButton(onClick = { /* action */ }) {
    Text("Skip")
}

// FloatingActionButton
FloatingActionButton(onClick = { /* action */ }) {
    Icon(Icons.Default.Add, contentDescription = "Add")
}

// IconButton
IconButton(onClick = { /* action */ }) {
    Icon(Icons.Default.Favorite, contentDescription = "Favorite")
}

// Button with icon + text
Button(onClick = { /* action */ }) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text("Send")
    }
}

// Disabled button
Button(onClick = {}, enabled = false) { Text("Disabled") }
```

---

## Q4: How do you use Image and Icon?

```kotlin
// Image from resource
Image(
    painter = painterResource(R.drawable.profile),
    contentDescription = "Profile photo",
    modifier = Modifier.size(100.dp).clip(CircleShape),
    contentScale = ContentScale.Crop,
)

// Image from URL (with Coil)
AsyncImage(
    model = "https://example.com/photo.jpg",
    contentDescription = "Network photo",
    modifier = Modifier.size(100.dp),
)

// Icon
Icon(
    imageVector = Icons.Default.Home,
    contentDescription = "Home",
    tint = MaterialTheme.colorScheme.primary,
    modifier = Modifier.size(24.dp),
)

// Icon from drawable
Icon(
    painter = painterResource(R.drawable.ic_custom),
    contentDescription = "Custom icon",
)

// contentDescription = null for decorative icons
Icon(Icons.Default.Search, contentDescription = null)  // Decorative
```

---

## Q5: How do you use Box, Column, and Row?

```kotlin
// Column — vertical arrangement
Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Text("First")
    Text("Second")
    Text("Third")
}

// Row — horizontal arrangement
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text("Left")
    Text("Right")
}

// Box — stacking (z-order)
Box(
    modifier = Modifier.size(200.dp),
    contentAlignment = Alignment.Center,
) {
    Image(painter = painterResource(R.drawable.bg), contentDescription = null)
    Text("Overlay", color = Color.White)
}

// Box with alignment per child
Box {
    Text("Top", modifier = Modifier.align(Alignment.TopStart))
    Text("Bottom", modifier = Modifier.align(Alignment.BottomEnd))
}
```

### Arrangement Options
| Column (vertical) | Row (horizontal) |
|-------------------|------------------|
| `Arrangement.Top` | `Arrangement.Start` |
| `Arrangement.Center` | `Arrangement.Center` |
| `Arrangement.Bottom` | `Arrangement.End` |
| `Arrangement.SpaceEvenly` | `Arrangement.SpaceEvenly` |
| `Arrangement.SpaceBetween` | `Arrangement.SpaceBetween` |
| `Arrangement.SpaceAround` | `Arrangement.SpaceAround` |
| `Arrangement.spacedBy(8.dp)` | `Arrangement.spacedBy(8.dp)` |

---

## Q6: How do you use Spacer and Divider?

```kotlin
// Spacer — add space between elements
Column {
    Text("First")
    Spacer(modifier = Modifier.height(16.dp))
    Text("Second")
}

Row {
    Text("Left")
    Spacer(modifier = Modifier.width(8.dp))
    Text("Right")
}

// Spacer with weight (fills remaining space)
Row {
    Text("Left")
    Spacer(modifier = Modifier.weight(1f))  // Takes all remaining space
    Text("Right")
}

// HorizontalDivider (Material 3)
Column {
    Text("Section 1")
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    Text("Section 2")
}
```

---

## Q7: How do you handle user input (TextField)?

```kotlin
// Basic TextField
@Composable
fun NameInput() {
    var name by remember { mutableStateOf("") }

    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
        placeholder = { Text("Enter your name") },
        singleLine = true,
    )
}

// OutlinedTextField
OutlinedTextField(
    value = email,
    onValueChange = { email = it },
    label = { Text("Email") },
    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
    trailingIcon = { Icon(Icons.Default.Clear, contentDescription = "Clear") },
    isError = !isValid,
    supportingText = { Text("Enter valid email") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
)

// Password field
var password by remember { mutableStateOf("") }
var visible by remember { mutableStateOf(false) }

OutlinedTextField(
    value = password,
    onValueChange = { password = it },
    label = { Text("Password") },
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    trailingIcon = {
        IconButton(onClick = { visible = !visible }) {
            Icon(
                if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle password",
            )
        }
    },
)
```

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Layouts](Layouts.md)
- [Modifiers](Modifiers.md)
