package com.wtc.crmconnect.app.ui.screens



import com.wtc.crmconnect.app.viewmodel.ClientesViewModel
import com.wtc.crmconnect.app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*
import com.wtc.crmconnect.app.model.GrupoCliente
import androidx.navigation.NavController

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

@Composable
fun ChatNewGroupScreen(
    navController: NavController,
    clientesViewModel: ClientesViewModel
) {
    val textoCinza = Color(0xFF757575)

    var searchText by remember { mutableStateOf("") }
    var nomeGrupo by remember { mutableStateOf("") }

    val clientes = clientesViewModel.clientesChat
    val clientesFiltrados = if (searchText.isBlank()) {
        clientes
    } else {
        clientes.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    val selectedClientes = remember { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        backgroundColor = Color.White,
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 56.dp + 44.dp + 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { navController.navigate("history_customers_screen") },
                                modifier = Modifier.offset(x = (-14).dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Color.Black
                                )
                            }
                            Text(
                                text = "Adicione os membros do novo Grupo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins,
                                color = Color.Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(x = (-12).dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            elevation = 8.dp
                        ) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                placeholder = {
                                    Text(
                                        "Nome do Cliente",
                                        color = Color.Gray,
                                        fontFamily = Poppins
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Pesquisar",
                                        tint = textoCinza
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    backgroundColor = Color.White,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedBorderColor = Color.Transparent,
                                    disabledBorderColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        elevation = 8.dp
                    ) {
                        OutlinedTextField(
                            value = nomeGrupo,
                            onValueChange = { nomeGrupo = it },
                            placeholder = {
                                Text(
                                    text = "Nome do Grupo",
                                    color = Color.Gray,
                                    fontFamily = Poppins
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor = Color.White,
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(fontFamily = Poppins)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                stickyHeader {
                    Column(modifier = Modifier.background(Color.White)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "CLIENTES",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(vertical = 8.dp)
                        )
                    }
                }

                itemsIndexed(clientesFiltrados) { index, cliente ->
                    GrupoClienteListItem(
                        cliente = cliente,
                        showDivider = index != clientesFiltrados.lastIndex,
                        isChecked = selectedClientes[cliente.id] ?: false,
                        onCheckChanged = { checked ->
                            selectedClientes[cliente.id] = checked
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        Button(
                            onClick = {
                                val selecionados =
                                    clientes.filter { selectedClientes[it.id] == true }

                                if (selecionados.size >= 2 && nomeGrupo.isNotBlank()) {
                                    clientesViewModel.criarGrupo(nomeGrupo, selecionados)
                                    navController.navigate("history_customers_screen")
                                }
                            },
                            modifier = Modifier
                                .width(111.dp)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF22394E)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Criar",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.poppins_medium))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GrupoClienteImage(
    imageRes: Int,
    circleSize: Dp = 48.dp,
    imageSize: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(circleSize)
            .clip(CircleShape)
            .border(2.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Foto do Cliente",
            modifier = Modifier.size(imageSize)
        )
    }
}

@Composable
fun GrupoDividerLine() {
    Divider(
        color = Color(0xFF0810A5),
        thickness = 1.dp,
        modifier = Modifier.padding(
            start = 0.dp, end = 60.dp,
            top = 10.dp, bottom = 10.dp
        )
    )
}

@Composable
fun CustomOutlinedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Black,
    checkmarkColor: Color = Color.Black
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "Selecionado",
                tint = checkmarkColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun GrupoClienteListItem(
    cliente: GrupoCliente,
    showDivider: Boolean,
    isChecked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {
    val density = LocalDensity.current
    val buttonWidthDp = 80.dp
    val maxOffsetPx = with(density) { buttonWidthDp.toPx() * 2 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable { onCheckChanged(!isChecked) }
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            GrupoClienteImage(
                imageRes = cliente.imageRes,
                circleSize = 48.dp,
                imageSize = 40.dp
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente.name,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            CustomOutlinedCheckbox(
                checked = isChecked,
                onCheckedChange = onCheckChanged,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
        }
    }

    if (showDivider) {
        GrupoDividerLine()
    }
}
